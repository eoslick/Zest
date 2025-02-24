package com.ses.zest.event.ports;

import com.ses.zest.common.*;
import java.util.List;

public interface EventStore {
    void append(Event event, TenantId tenantId);
    List<Event> getEvents(EntityId<?> entityId, TenantId tenantId, AccountId accountId);
}