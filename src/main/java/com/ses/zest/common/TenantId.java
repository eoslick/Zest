package com.ses.zest.common;

import java.io.Serializable;
import java.util.UUID;

public final class TenantId extends EntityId<TenantId> implements Serializable {
    private static final long serialVersionUID = 1L;
    public TenantId() {
        super();
    }
    public TenantId(UUID value) {
        super(value);
    }
}