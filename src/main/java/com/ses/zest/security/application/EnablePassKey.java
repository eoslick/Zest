package com.ses.zest.security.application;

import com.ses.zest.common.TenantId;
import com.ses.zest.user.domain.UserId;
import com.ses.zest.security.domain.Credentials;
import com.ses.zest.security.ports.AuthenticationRepository;

public final class EnablePassKey {
    private final AuthenticationRepository authRepo;

    public EnablePassKey(AuthenticationRepository authRepo) {
        this.authRepo = authRepo;
    }

    public void execute(UserId userId, TenantId tenantId, String publicKey) {
        Credentials current = authRepo.getAuthenticationData(userId, tenantId);
        if (current == null) {
            authRepo.storeAuthenticationData(userId, tenantId, new Credentials(null, null, publicKey));
        } else {
            authRepo.storeAuthenticationData(userId, tenantId, new Credentials(current.hashedPassword(), current.totpSecret(), publicKey));
        }
    }
}