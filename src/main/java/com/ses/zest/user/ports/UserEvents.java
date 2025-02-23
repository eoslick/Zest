package com.ses.zest.user.ports;

import com.ses.zest.common.Event;

public interface UserEvents {
    void publish(Event event);
}