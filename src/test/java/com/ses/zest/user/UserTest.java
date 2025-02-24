package com.ses.zest.user;

import com.ses.zest.event.ports.EventStore;
import com.ses.zest.user.application.*;
import com.ses.zest.user.domain.*;
import com.ses.zest.user.adapters.*;
import com.ses.zest.common.*;
import com.ses.zest.event.adapters.*;
import com.ses.zest.event.ports.EventSubscriber;
import com.ses.zest.encryption.adapters.*;
import com.ses.zest.security.adapters.BasicRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private static final Path TEST_EVENTS_DIR = Paths.get("target/test-events");

    @BeforeEach
    void setUp() throws IOException {
        if (Files.exists(TEST_EVENTS_DIR)) {
            Files.walk(TEST_EVENTS_DIR)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to delete " + path, e);
                        }
                    });
        }
        Files.createDirectories(TEST_EVENTS_DIR);
    }

    private static Stream<EventSubscriber> eventStores() {
        var encryption = new AesEncryption();
        var keyManager = new InMemoryKeyManager(encryption);
        return Stream.of(
                new InMemoryEventStore(encryption, keyManager),
                new FileBasedEventStore(TEST_EVENTS_DIR, encryption, keyManager)
        );
    }

    @ParameterizedTest
    @MethodSource("eventStores")
    void shouldCreateUserAndPublishEvent(EventSubscriber eventStore) {
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
        eventBus.shutdown();

        User user = users.find(userId, tenantId);
        assertNotNull(user, "User should be saved");
        List<Event> storedEvents = ((EventStore) eventStore).getEvents(userId, tenantId, accountId);
        assertEquals(1, storedEvents.size(), "One event should be stored");
        assertEquals(email, ((UserCreated) storedEvents.get(0)).email(), "Event should contain email");
    }

    @ParameterizedTest
    @MethodSource("eventStores")
    void shouldEncryptAndDecryptEventsCorrectly(EventSubscriber eventStore) {
        var eventBus = new InMemoryEventBus();
        eventBus.subscribe(eventStore);
        var users = new InMemoryUsers();
        var userEvents = new InMemoryUserEvents(eventBus);

        var createUser = new CreateUser(users, userEvents);
        var tenantId = new TenantId();
        var accountId = new AccountId();
        var wrongAccountId = new AccountId();
        var userId = new UserId();
        var email = new Email("encrypt@test.com");

        createUser.execute(userId, tenantId, accountId, email, BasicRole.TENANT_ADMIN);
        eventBus.shutdown();

        List<Event> correctEvents = ((EventStore) eventStore).getEvents(userId, tenantId, accountId);
        assertEquals(1, correctEvents.size(), "Event should be stored and decrypted with correct key");
        assertEquals(email, ((UserCreated) correctEvents.get(0)).email(), "Decrypted event should match");

        assertThrows(RuntimeException.class, () ->
                        ((EventStore) eventStore).getEvents(userId, tenantId, wrongAccountId),
                "Decrypting with wrong key should fail");
    }

    @ParameterizedTest
    @MethodSource("eventStores")
    void shouldRetrieveOnlyTenantSpecificEvents(EventSubscriber eventStore) {
        var eventBus = new InMemoryEventBus();
        eventBus.subscribe(eventStore);
        var users = new InMemoryUsers();
        var userEvents = new InMemoryUserEvents(eventBus);

        var createUser = new CreateUser(users, userEvents);
        var tenant1 = new TenantId();
        var tenant2 = new TenantId();
        var accountId = new AccountId();
        var userId1 = new UserId();
        var userId2 = new UserId();

        createUser.execute(userId1, tenant1, accountId, new Email("tenant1@test.com"), BasicRole.TENANT_ADMIN);
        createUser.execute(userId2, tenant2, accountId, new Email("tenant2@test.com"), BasicRole.TENANT_ADMIN);
        eventBus.shutdown();

        List<Event> tenant1Events = ((EventStore) eventStore).getEvents(userId1, tenant1, accountId);
        assertEquals(1, tenant1Events.size(), "Only tenant1's event should be retrieved");
        assertEquals("tenant1@test.com", ((UserCreated) tenant1Events.get(0)).email().value());

        List<Event> tenant2Events = ((EventStore) eventStore).getEvents(userId2, tenant2, accountId);
        assertEquals(1, tenant2Events.size(), "Only tenant2's event should be retrieved");
        assertEquals("tenant2@test.com", ((UserCreated) tenant2Events.get(0)).email().value());
    }

    @ParameterizedTest
    @MethodSource("eventStores")
    void shouldDeleteUserAndPublishEvent(EventSubscriber eventStore) {
        var eventBus = new InMemoryEventBus();
        eventBus.subscribe(eventStore);
        var users = new InMemoryUsers();
        var userEvents = new InMemoryUserEvents(eventBus);

        var createUser = new CreateUser(users, userEvents);
        var deleteUser = new DeleteUser(users, userEvents);
        var tenantId = new TenantId();
        var accountId = new AccountId();
        var userId = new UserId();
        var email = new Email("delete@test.com");

        createUser.execute(userId, tenantId, accountId, email, BasicRole.TENANT_ADMIN);
        deleteUser.execute(userId, tenantId, accountId, BasicRole.TENANT_ADMIN);
        eventBus.shutdown();

        User user = users.find(userId, tenantId);
        assertTrue(user.isDeleted(), "User should be marked as deleted");
        List<Event> storedEvents = ((EventStore) eventStore).getEvents(userId, tenantId, accountId);
        assertEquals(2, storedEvents.size(), "Two events (create and delete) should be stored");
        assertInstanceOf(UserCreated.class, storedEvents.get(0), "First event should be UserCreated");
        assertInstanceOf(UserDeleted.class, storedEvents.get(1), "Second event should be UserDeleted");
    }
}