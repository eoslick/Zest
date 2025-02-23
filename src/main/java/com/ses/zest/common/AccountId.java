package com.ses.zest.common;

import java.util.UUID;

public final class AccountId extends EntityId<AccountId> {
    public AccountId() {
        super();
    }
    public AccountId(UUID value) {
        super(value);
    }
}