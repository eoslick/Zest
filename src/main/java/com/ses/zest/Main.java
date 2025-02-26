package com.ses.zest;

import com.ses.zest.security.adapters.InMemoryAuthenticationRepository;
import com.ses.zest.user.application.CreateUser;
import com.ses.zest.user.domain.Email;
import com.ses.zest.user.domain.UserId; // New import
import com.ses.zest.user.adapters.InMemoryUsers;
import com.ses.zest.user.adapters.InMemoryUserEvents;
import com.ses.zest.common.*;
import com.ses.zest.event.adapters.InMemoryEventBus;
import com.ses.zest.event.adapters.InMemoryEventStore;
import com.ses.zest.encryption.adapters.AesEncryption;
import com.ses.zest.encryption.adapters.InMemoryKeyManager;
import com.ses.zest.security.adapters.BasicRole;

public final class Main {
    public static void main(String[] args) {
        // Dependencies
        var encryption = new AesEncryption();
        var keyManager = new InMemoryKeyManager(encryption);
        var eventStore = new InMemoryEventStore(encryption, keyManager);
        var eventBus = new InMemoryEventBus();
        var users = new InMemoryUsers();
        var userEvents = new InMemoryUserEvents(eventBus);
        var authRepo = new InMemoryAuthenticationRepository(); // Added

        // Use Case
        var createUser = new CreateUser(users, userEvents, authRepo); // Updated
        var tenantId = new TenantId();
        var accountId = new AccountId();
        var userId = new UserId();
        var email = new Email("user@example.com");
        var password = "securePass123"; // Added password

        createUser.execute(userId, tenantId, accountId, email, password, BasicRole.TENANT_ADMIN);

        System.out.println("User created and event published!");
    }
}