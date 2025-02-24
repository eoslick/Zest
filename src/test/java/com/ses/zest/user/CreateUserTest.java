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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

 class CreateUserTest {
     @Test
     void shouldCreateUserAndPublishEvent() {
         var encryption = new AesEncryption();
         var keyManager = new InMemoryKeyManager(encryption);
         var eventStore = new InMemoryEventStore(encryption, keyManager);
         var eventBus = new InMemoryEventBus();
         eventBus.subscribe(eventStore);
         var users = new InMemoryUsers();
         var userEvents = new InMemoryUserEvents(eventBus);

         var createUser = new CreateUser(users, userEvents);
         var tenantId = new TenantId();
         var accountId = new AccountId();
         var userId = new UserId();
         var email = new Email("test@example.com");
         var role = BasicRole.TENANT_ADMIN;

         createUser.execute(userId, tenantId, accountId, email, role);

         User user = users.find(userId, tenantId);
         assertNotNull(user, "User should be saved");
         List<Event> storedEvents = eventStore.getEvents(userId, tenantId, accountId);
         assertEquals(1, storedEvents.size(), "One event should be stored");
         assertEquals(email, ((UserCreated) storedEvents.get(0)).email(), "Event should contain email");
     }
}