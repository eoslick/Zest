package com.ses.zest.user.application;

import com.ses.zest.user.domain.*;
import com.ses.zest.user.ports.*;
import com.ses.zest.common.*;
import com.ses.zest.security.domain.Role;

public final class DeleteUser {
    private final Users users;
    private final UserEvents userEvents;

    public DeleteUser(Users users, UserEvents userEvents) {
        this.users = users;
        this.userEvents = userEvents;
    }

    public void execute(UserId id, TenantId tenantId, AccountId accountId, Role role) {
        User user = users.find(id, tenantId);
        if (user == null) throw new IllegalStateException("User not found");
        user.delete(role);
        users.save(user, tenantId);
        user.uncommittedEvents().forEach(userEvents::publish);
    }
}