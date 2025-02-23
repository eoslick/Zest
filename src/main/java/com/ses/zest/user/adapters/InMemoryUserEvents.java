package com.ses.zest.user.adapters;

import com.ses.zest.user.ports.UserEvents;
import com.ses.zest.common.Event;
import com.ses.zest.event.ports.EventBus;

public final class InMemoryUserEvents implements UserEvents {
    private final EventBus eventBus;

    public InMemoryUserEvents(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void publish(Event event) {
        eventBus.publish(event);
    }
}