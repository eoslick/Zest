// src/main/java/com/ses/zest/security/application/EnableSocialLogin.java
package com.ses.zest.security.application;

import com.ses.zest.common.TenantId;
import com.ses.zest.user.domain.UserId;
import com.ses.zest.security.domain.Credentials;
import com.ses.zest.security.domain.SocialLoginVerifier;
import com.ses.zest.security.ports.AuthenticationRepository;

public final class EnableSocialLogin {
    private final AuthenticationRepository authRepo;

    public EnableSocialLogin(AuthenticationRepository authRepo) {
        this.authRepo = authRepo;
    }

    public void execute(UserId userId, TenantId tenantId, String provider, String idToken) {
        boolean validToken = switch (provider) {
            case "google" -> SocialLoginVerifier.verifyGoogleToken(idToken);
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };
        if (!validToken) {
            throw new IllegalStateException("Invalid social login token");
        }
        String socialProviderId = SocialLoginVerifier.extractUserId(idToken);
        Credentials current = authRepo.getAuthenticationData(userId, tenantId);
        if (current == null) {
            authRepo.storeAuthenticationData(userId, tenantId, new Credentials(null, null, null, socialProviderId));
        } else {
            authRepo.storeAuthenticationData(userId, tenantId, new Credentials(
                    current.hashedPassword(), current.totpSecret(), current.passKeyPublicKey(), socialProviderId));
        }
    }
}