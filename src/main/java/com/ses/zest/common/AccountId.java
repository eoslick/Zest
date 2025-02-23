package com.ses.zest.common;

import java.io.Serializable;
import java.util.UUID;

public final class AccountId extends EntityId<AccountId> implements Serializable {
    private static final long serialVersionUID = 1L;
    public AccountId() {
        super();
    }
    public AccountId(UUID value) {
        super(value);
    }
}