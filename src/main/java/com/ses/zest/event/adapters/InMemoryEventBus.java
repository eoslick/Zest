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
        executor.submit(() -> subscribers.forEach(sub -> sub.handle(event)));
    }

    @Override
    public void subscribe(EventSubscriber subscriber) {
        subscribers.add(subscriber);
    }
}