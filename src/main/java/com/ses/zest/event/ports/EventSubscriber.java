package com.ses.zest.event.ports;

import com.ses.zest.common.Event;

public interface EventSubscriber {
    void handle(Event event);
}