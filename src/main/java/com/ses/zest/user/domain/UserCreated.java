package com.ses.zest.user.domain;

import com.ses.zest.common.*;

import java.io.Serial;
import java.io.Serializable;

public final class UserCreated extends Event implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Email email;

    public UserCreated(EntityId<?> entityId, TenantId tenantId, AccountId accountId, Email email) {
        super(entityId, tenantId, accountId);
        this.email = email;
    }

    public Email email() { return email; }
}