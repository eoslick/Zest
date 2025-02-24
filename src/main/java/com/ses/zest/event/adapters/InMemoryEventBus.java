package com.ses.zest.event.adapters;

import com.ses.zest.event.ports.*;
import com.ses.zest.common.Event;

import java.util.*;
import java.util.concurrent.*;

public final class InMemoryEventBus implements EventBus {
    private final List<EventSubscriber> subscribers = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void publish(Event event) {
        CountDownLatch latch = new CountDownLatch(subscribers.size());
        executor.submit(() -> {
            subscribers.forEach(sub -> {
                sub.handle(event);
                latch.countDown();
            });
        });
        try {
            latch.await(1, TimeUnit.SECONDS); // Wait for subscribers to finish
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for event publishing", e);
        }
    }

    @Override
    public void subscribe(EventSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    // For cleanup in tests (optional)
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}