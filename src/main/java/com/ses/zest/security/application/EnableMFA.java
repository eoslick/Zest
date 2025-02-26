// src/main/java/com/ses/zest/security/application/EnableMFA.java
package com.ses.zest.security.application;

import com.ses.zest.common.TenantId;
import com.ses.zest.user.domain.UserId;
import com.ses.zest.security.domain.Credentials;
import com.ses.zest.security.domain.TOTPVerifier;
import com.ses.zest.security.ports.AuthenticationRepository;

public final class EnableMFA {
    private final AuthenticationRepository authRepo;

    public EnableMFA(AuthenticationRepository authRepo) {
        this.authRepo = authRepo;
    }

    public String execute(UserId userId, TenantId tenantId) {
        Credentials current = authRepo.getAuthenticationData(userId, tenantId);
        if (current == null || current.hashedPassword() == null) {
            throw new IllegalStateException("User must have a password to enable MFA");
        }
        String totpSecret = TOTPVerifier.generateSecret();
        authRepo.storeAuthenticationData(userId, tenantId, new Credentials(
                current.hashedPassword(),      // Keep the existing hashed password
                totpSecret,                    // The new TOTP secret for MFA
                current.passKeyPublicKey(),    // Keep the existing passkey public key
                current.socialProviderId()     // Keep the existing social provider ID
        ));        return totpSecret; // Return secret for user to configure (e.g., QR code)
    }
}