package com.ses.zest.user.adapters;

import com.ses.zest.user.domain.User;
import com.ses.zest.user.domain.UserId;
import com.ses.zest.user.ports.Users;
import com.ses.zest.common.*;

import java.util.HashMap;
import java.util.Map;

public final class InMemoryUsers implements Users {
    private final Map<TenantId, Map<UserId, User>> users = new HashMap<>(); // Updated to UserId

    @Override
    public void save(User user, TenantId tenantId) {
        users.computeIfAbsent(tenantId, k -> new HashMap<>()).put(user.id(), user);
    }

    @Override
    public User find(UserId id, TenantId tenantId) { // Updated to UserId
        return users.getOrDefault(tenantId, Map.of()).get(id);
    }
}