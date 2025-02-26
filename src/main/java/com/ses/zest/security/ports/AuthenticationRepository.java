package com.ses.zest.security.ports;

import com.ses.zest.common.TenantId;
import com.ses.zest.common.AccountId;
import com.ses.zest.security.domain.Credentials;
import com.ses.zest.user.domain.UserId;

public interface AuthenticationRepository {
    void storeAuthenticationData(UserId userId, TenantId tenantId, Credentials data);
    Credentials getAuthenticationData(UserId userId, TenantId tenantId);
}
