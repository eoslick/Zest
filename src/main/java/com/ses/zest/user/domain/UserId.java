package com.ses.zest.user.domain;

import com.ses.zest.common.EntityId;
import java.util.UUID;

public final class UserId extends EntityId<UserId> {
    public UserId() { super(); }
    public UserId(UUID value) { super(value); }
}