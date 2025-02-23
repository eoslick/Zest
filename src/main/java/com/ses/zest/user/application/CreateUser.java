package com.ses.zest.user.application;

import com.ses.zest.user.domain.*;
import com.ses.zest.user.ports.*;
import com.ses.zest.common.*;
import com.ses.zest.security.domain.Role;

public final class CreateUser {
    private final Users users;
    private final UserEvents userEvents;

    public CreateUser(Users users, UserEvents userEvents) {
        this.users = users;
        this.userEvents = userEvents;
    }

    public void execute(UserId id, TenantId tenantId, AccountId accountId, Email email, Role role) {
        var user = new User(id, tenantId, accountId);
        user.create(email, role);
        users.save(user, tenantId);
        user.uncommittedEvents().forEach(userEvents::publish);
    }
}