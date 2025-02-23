package com.ses.zest.event.ports;

import com.ses.zest.common.Event;

public interface EventBus {
    void publish(Event event);
    void subscribe(EventSubscriber subscriber);
}