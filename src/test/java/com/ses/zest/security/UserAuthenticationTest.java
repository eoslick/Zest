// src/test/java/com/ses/zest/security/UserAuthenticationTest.java
package com.ses.zest.security;

import com.ses.zest.common.*;
import com.ses.zest.security.adapters.BasicRole;
import com.ses.zest.security.adapters.InMemoryAuthenticationRepository;
import com.ses.zest.security.application.AuthenticationService;
import com.ses.zest.security.application.EnableMFA;
import com.ses.zest.security.application.EnablePassKey;
import com.ses.zest.security.domain.AuthenticationResult;
import com.ses.zest.security.domain.PasswordHasher;
import com.ses.zest.security.domain.TOTPVerifier;
import com.ses.zest.user.adapters.InMemoryUserEvents;
import com.ses.zest.user.adapters.InMemoryUsers;
import com.ses.zest.user.application.CreateUser;
import com.ses.zest.user.domain.*;
import com.ses.zest.event.adapters.InMemoryEventBus;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class UserAuthenticationTest {
    @Test
    void shouldAuthenticateUserWithPassword() {
        var authRepo = new InMemoryAuthenticationRepository();
        var users = new InMemoryUsers();
        var userEvents = new InMemoryUserEvents(new InMemoryEventBus());
        var createUser = new CreateUser(users, userEvents, authRepo);
        var authService = new AuthenticationService(authRepo, users);

        var userId = new UserId();
        var tenantId = new TenantId();
        var accountId = new AccountId();
        var email = new Email("test@example.com");
        var password = "securePass123";
        createUser.execute(userId, tenantId, accountId, email, password, BasicRole.TENANT_ADMIN);

        var result = authService.authenticatePassword(userId, tenantId, password);
        assertInstanceOf(AuthenticationResult.Success.class, result);
        User user = ((AuthenticationResult.Success) result).user();
        assertEquals(email, user.email());

        var wrongResult = authService.authenticatePassword(userId, tenantId, "wrongPass");
        assertInstanceOf(AuthenticationResult.Failure.class, wrongResult);
        assertEquals("Invalid password", ((AuthenticationResult.Failure) wrongResult).reason());
    }

    @Test
    void shouldAuthenticateUserWithTOTP() {
        var authRepo = new InMemoryAuthenticationRepository();
        var users = new InMemoryUsers();
        var userEvents = new InMemoryUserEvents(new InMemoryEventBus());
        var createUser = new CreateUser(users, userEvents, authRepo);
        var enableMFA = new EnableMFA(authRepo);
        var authService = new AuthenticationService(authRepo, users);

        var userId = new UserId();
        var tenantId = new TenantId();
        var accountId = new AccountId();
        var email = new Email("mfa@example.com");
        var password = "securePass123";
        createUser.execute(userId, tenantId, accountId, email, password, BasicRole.TENANT_ADMIN);

        String totpSecret = enableMFA.execute(userId, tenantId);
        var passwordResult = authService.authenticatePassword(userId, tenantId, password);
        assertInstanceOf(AuthenticationResult.MFARequired.class, passwordResult);
        assertEquals("totp", ((AuthenticationResult.MFARequired) passwordResult).method());

        String totpCode = TOTPVerifier.generateCode(totpSecret);
        var totpResult = authService.authenticateTOTP(userId, tenantId, totpCode);
        assertInstanceOf(AuthenticationResult.Success.class, totpResult);
        User user = ((AuthenticationResult.Success) totpResult).user();
        assertEquals(email, user.email());

        var wrongTotpResult = authService.authenticateTOTP(userId, tenantId, "000000");
        assertInstanceOf(AuthenticationResult.Failure.class, wrongTotpResult);
        assertEquals("Invalid TOTP code", ((AuthenticationResult.Failure) wrongTotpResult).reason());
    }

    @Test
    void shouldAuthenticateUserWithPassKey() {
        var authRepo = new InMemoryAuthenticationRepository();
        var users = new InMemoryUsers();
        var userEvents = new InMemoryUserEvents(new InMemoryEventBus());
        var createUser = new CreateUser(users, userEvents, authRepo);
        var enablePassKey = new EnablePassKey(authRepo);
        var authService = new AuthenticationService(authRepo, users);

        var userId = new UserId();
        var tenantId = new TenantId();
        var accountId = new AccountId();
        var email = new Email("passkey@example.com");
        var password = "securePass123";
        createUser.execute(userId, tenantId, accountId, email, password, BasicRole.TENANT_ADMIN);

        // Enable PassKey (simulated public key)
        String publicKey = "fake-public-key";
        enablePassKey.execute(userId, tenantId, publicKey);

        // PassKey step
        String challenge = authService.generatePassKeyChallenge();
        String signedChallenge = "signed-" + publicKey; // Simulated signature
        var passKeyResult = authService.authenticatePassKey(userId, tenantId, signedChallenge);
        assertInstanceOf(AuthenticationResult.Success.class, passKeyResult);
        User user = ((AuthenticationResult.Success) passKeyResult).user();
        assertEquals(email, user.email());

        // Wrong signature
        var wrongPassKeyResult = authService.authenticatePassKey(userId, tenantId, "wrong-signature");
        assertInstanceOf(AuthenticationResult.Failure.class, wrongPassKeyResult);
        assertEquals("Invalid PassKey signature", ((AuthenticationResult.Failure) wrongPassKeyResult).reason());
    }
}