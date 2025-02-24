package com.ses.zest.user;

import com.ses.zest.event.ports.EventStore;
import com.ses.zest.user.application.CreateUser;
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

public class CreateUserTest {
    private static final Path TEST_EVENTS_DIR = Paths.get("target/test-events");

    @BeforeEach
    void setUp() throws IOException {
        // Clean up test-events directory before each test
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

        User user = users.find(userId, tenantId);
        assertNotNull(user, "User should be saved");
        List<Event> storedEvents = ((EventStore) eventStore).getEvents(userId, tenantId, accountId);
        assertEquals(1, storedEvents.size(), "One event should be stored");
        assertEquals(email, ((UserCreated) storedEvents.get(0)).email(), "Event should contain email");
    }
}