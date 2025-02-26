package com.ses.zest.user.domain;

import com.ses.zest.common.EntityId;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public final class UserId extends EntityId<UserId> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public UserId() { super(); }
    public UserId(UUID value) { super(value); }
}