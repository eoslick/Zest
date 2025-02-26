package com.ses.zest.user.domain;

import com.ses.zest.common.*;
import com.ses.zest.security.domain.Role;

import java.util.ArrayList;
import java.util.List;

public final class User {
    private final UserId id;
    private final TenantId tenantId;
    private final AccountId accountId;
    private Email email;
    private boolean deleted; // Track deletion state
    private final List<Event> uncommittedEvents = new ArrayList<>();

    public User(UserId id, TenantId tenantId, AccountId accountId) {
        this.id = id;
        this.tenantId = tenantId;
        this.accountId = accountId;
        this.deleted = false;
    }

    public void create(Email email, Role role) {
        if (!role.canPerform("create-user")) throw new SecurityException("Unauthorized");
        if (deleted) throw new IllegalStateException("User is deleted");
        this.email = email;
        uncommittedEvents.add(new UserCreated(id, tenantId, accountId, email));
    }

    public void delete(Role role) {
        if (!role.canPerform("delete-user")) throw new SecurityException("Unauthorized");
        if (deleted) throw new IllegalStateException("User already deleted");
        this.deleted = true;
        uncommittedEvents.add(new UserDeleted(id, tenantId, accountId));
    }

    public UserId id() { return id; }
    public boolean isDeleted() { return deleted; }
    public Email email() { return email; } // Added getter

    public List<Event> uncommittedEvents() {
        List<Event> events = List.copyOf(uncommittedEvents);
        uncommittedEvents.clear(); // Clear after retrieval
        return events;
    }
}