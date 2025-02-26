package com.ses.zest.security.adapters;

import com.ses.zest.common.TenantId;
import com.ses.zest.user.domain.UserId;
import com.ses.zest.security.domain.Credentials;
import com.ses.zest.security.ports.AuthenticationRepository;

import java.util.HashMap;
import java.util.Map;

public final class InMemoryAuthenticationRepository implements AuthenticationRepository {
    private final Map<TenantId, Map<UserId, Credentials>> data = new HashMap<>();

    @Override
    public void storeAuthenticationData(UserId userId, TenantId tenantId, Credentials credentials) {
        data.computeIfAbsent(tenantId, k -> new HashMap<>()).put(userId, credentials);
    }

    @Override
    public Credentials getAuthenticationData(UserId userId, TenantId tenantId) {
        return data.getOrDefault(tenantId, Map.of()).get(userId);
    }
}