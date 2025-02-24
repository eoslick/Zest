package com.ses.zest.common;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

public abstract class Event implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final EntityId<?> entityId;
    private final TenantId tenantId;
    private final AccountId accountId;
    private final Instant timestamp;

    protected Event(EntityId<?> entityId, TenantId tenantId, AccountId accountId) {
        this.entityId = entityId;
        this.tenantId = tenantId;
        this.accountId = accountId;
        this.timestamp = Instant.now();
    }

    public EntityId<?> entityId() { return entityId; }
    public TenantId tenantId() { return tenantId; }
    public AccountId userId() { return accountId; } // Rename if needed
    public Instant timestamp() { return timestamp; }
}