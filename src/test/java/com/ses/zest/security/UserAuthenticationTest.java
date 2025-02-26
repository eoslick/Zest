package com.ses.zest.security;

import com.ses.zest.common.*;
import com.ses.zest.security.adapters.BasicRole;
import com.ses.zest.security.adapters.InMemoryAuthenticationRepository;
import com.ses.zest.security.application.AuthenticationService;
import com.ses.zest.security.domain.AuthenticationResult;
import com.ses.zest.security.domain.PasswordHasher;
import com.ses.zest.user.adapters.InMemoryUserEvents;
import com.ses.zest.user.adapters.InMemoryUsers;
import com.ses.zest.user.application.CreateUser;
import com.ses.zest.user.domain.*;
import com.ses.zest.event.adapters.InMemoryEventBus;
import org.junit.jupiter.api.Test;

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
}