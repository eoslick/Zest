package com.ses.zest.event.adapters;

import com.ses.zest.common.Event;
import com.ses.zest.common.TenantId;

import java.io.Serial;
import java.io.Serializable;

public record StoredEvent(TenantId tenantId, byte[] encryptedData) implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;
}