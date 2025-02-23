package com.ses.zest.user.domain;

import com.ses.zest.common.*;

public final class UserCreated extends Event {
    private final Email email;

    public UserCreated(EntityId<?> entityId, TenantId tenantId, AccountId accountId, Email email) {
        super(entityId, tenantId, accountId); // Updated from UserId
        this.email = email;
    }

    public Email email() { return email; }
}