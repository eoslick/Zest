package com.ses.zest.user.domain;

import com.ses.zest.common.*;
import com.ses.zest.security.domain.Role;

import java.util.ArrayList;
import java.util.List;

public final class User {
    private final UserId id; // Updated to UserId
    private final TenantId tenantId;
    private final AccountId accountId; // Updated from UserId
    private Email email;
    private final List<Event> uncommittedEvents = new ArrayList<>();

    public User(UserId id, TenantId tenantId, AccountId accountId) {
        this.id = id;
        this.tenantId = tenantId;
        this.accountId = accountId;
    }

    public void create(Email email, Role role) {
        if (!role.canPerform("create-user")) throw new SecurityException("Unauthorized");
        this.email = email;
        uncommittedEvents.add(new UserCreated(id, tenantId, accountId, email));
    }

    public UserId id() { return id; }
    public List<Event> uncommittedEvents() { return List.copyOf(uncommittedEvents); }
}