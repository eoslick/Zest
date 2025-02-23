package com.ses.zest.common;

import java.util.UUID;

public final class TenantId extends EntityId<TenantId> {
    public TenantId() {
        super();
    }
    public TenantId(UUID value) {
        super(value);
    }
}