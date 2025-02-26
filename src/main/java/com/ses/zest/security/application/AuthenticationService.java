package com.ses.zest.security.application;

import com.ses.zest.common.TenantId;
import com.ses.zest.user.domain.UserId;
import com.ses.zest.security.domain.AuthenticationResult;
import com.ses.zest.security.domain.Credentials;
import com.ses.zest.security.domain.PasswordHasher;
import com.ses.zest.security.domain.TOTPVerifier;
import com.ses.zest.security.ports.AuthenticationRepository;
import com.ses.zest.user.domain.User;
import com.ses.zest.user.ports.Users;

public final class AuthenticationService {
    private final AuthenticationRepository authRepo;
    private final Users users;

    public AuthenticationService(AuthenticationRepository authRepo, Users users) {
        this.authRepo = authRepo;
        this.users = users;
    }

    public AuthenticationResult authenticatePassword(UserId userId, TenantId tenantId, String password) {
        Credentials data = authRepo.getAuthenticationData(userId, tenantId);
        if (data == null || data.hashedPassword() == null) {
            return new AuthenticationResult.Failure("User not found or no password set");
        }
        if (PasswordHasher.verifyPassword(password, data.hashedPassword())) {
            if (data.totpSecret() != null) {
                return new AuthenticationResult.MFARequired("totp");
            } else {
                User user = users.find(userId, tenantId);
                return new AuthenticationResult.Success(user);
            }
        } else {
            return new AuthenticationResult.Failure("Invalid password");
        }
    }

    public AuthenticationResult authenticateTOTP(UserId userId, TenantId tenantId, String totpCode) {
        Credentials data = authRepo.getAuthenticationData(userId, tenantId);
        if (data == null || data.totpSecret() == null) {
            return new AuthenticationResult.Failure("MFA not enabled");
        }
        if (TOTPVerifier.verify(totpCode, data.totpSecret())) {
            User user = users.find(userId, tenantId);
            return new AuthenticationResult.Success(user);
        } else {
            return new AuthenticationResult.Failure("Invalid TOTP code");
        }
    }
}
