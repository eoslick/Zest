package com.ses.zest.user.domain;

import com.ses.zest.common.*;

import java.io.Serial;
import java.io.Serializable;

public final class UserDeleted extends Event implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public UserDeleted(EntityId<?> entityId, TenantId tenantId, AccountId accountId) {
        super(entityId, tenantId, accountId);
    }
}