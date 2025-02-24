package com.ses.zest.event.adapters;

import com.ses.zest.common.Event;
import com.ses.zest.common.TenantId;

public record StoredEvent(TenantId tenantId, byte[] encryptedData) {
}