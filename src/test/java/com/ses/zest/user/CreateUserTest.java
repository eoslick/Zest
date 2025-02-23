package com.ses.zest.user;

import com.ses.zest.user.application.CreateUser;
import com.ses.zest.user.domain.*;
import com.ses.zest.user.adapters.InMemoryUsers;
import com.ses.zest.user.adapters.InMemoryUserEvents;
import com.ses.zest.common.*;
import com.ses.zest.event.adapters.InMemoryEventBus;
import com.ses.zest.event.adapters.InMemoryEventStore;
import com.ses.zest.encryption.adapters.AesEncryption;
import com.ses.zest.encryption.adapters.InMemoryKeyManager;
import com.ses.zest.security.adapters.BasicRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateUserTest {
    @Test
    void shouldCreateUserAndPublishEvent() {
        // Arrange
        var encryption = new AesEncryption();
        var keyManager = new InMemoryKeyManager(encryption);
        var eventStore = new InMemoryEventStore(encryption, keyManager);
        var eventBus = new InMemoryEventBus();
        var users = new InMemoryUsers();
        var userEvents = new InMemoryUserEvents(eventBus);

        var createUser = new CreateUser(users, userEvents);
        var tenantId = new TenantId();
        var accountId = new AccountId();
        var userId = new UserId();
        var email = new Email("test@example.com");
        var role = BasicRole.TENANT_ADMIN;

        // Act
        createUser.execute(userId, tenantId, accountId, email, role);

        // Assert
        User user = users.find(userId, tenantId);
        assertNotNull(user, "User should be saved");
        assertEquals(email, ((UserCreated)user.uncommittedEvents().get(0)).email(), "Event should contain email");
        assertEquals(1, user.uncommittedEvents().size(), "One event should be published");
    }
}