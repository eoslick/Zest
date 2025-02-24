package com.ses.zest.user;

import com.ses.zest.event.ports.EventStore;
import com.ses.zest.user.application.CreateUser;
import com.ses.zest.user.domain.*;
import com.ses.zest.user.adapters.*;
import com.ses.zest.common.*;
import com.ses.zest.event.adapters.*;
import com.ses.zest.event.ports.EventSubscriber; // Add this import
import com.ses.zest.encryption.adapters.*;
import com.ses.zest.security.adapters.BasicRole;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.*;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CreateUserTest {
    private static Stream<EventSubscriber> eventStores() { // Change to EventSubscriber
        var encryption = new AesEncryption();
        var keyManager = new InMemoryKeyManager(encryption);
        return Stream.of(
                new InMemoryEventStore(encryption, keyManager),
                new FileBasedEventStore(Paths.get("target/test-events"), encryption, keyManager)
        );
    }

    @ParameterizedTest
    @MethodSource("eventStores")
    void shouldCreateUserAndPublishEvent(EventSubscriber eventStore) { // Change to EventSubscriber
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
        // Cast to EventStore for getEvents
        List<Event> storedEvents = ((EventStore) eventStore).getEvents(userId, tenantId, accountId);
        assertEquals(1, storedEvents.size(), "One event should be stored");
        assertEquals(email, ((UserCreated) storedEvents.get(0)).email(), "Event should contain email");
    }
}