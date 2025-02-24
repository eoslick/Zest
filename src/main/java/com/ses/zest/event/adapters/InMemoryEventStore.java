package com.ses.zest.event.adapters;

import com.ses.zest.event.ports.*;
import com.ses.zest.common.*;
import com.ses.zest.encryption.ports.*;

import java.io.*;
import java.util.*;

public final class InMemoryEventStore implements EventStore, EventSubscriber {
    private final Map<TenantId, Map<EntityId<?>, List<StoredEvent>>> events = new HashMap<>();
    private final EncryptionAlgorithm encryption;
    private final KeyManager keyManager;

    public InMemoryEventStore(EncryptionAlgorithm encryption, KeyManager keyManager) {
        this.encryption = encryption;
        this.keyManager = keyManager;
    }

    @Override
    public void append(Event event, TenantId tenantId) {
        byte[] serializedEvent = serialize(event);
        byte[] encryptedData = encryption.encrypt(serializedEvent, keyManager.getDataKey(event.userId()));
        StoredEvent storedEvent = new StoredEvent(tenantId, encryptedData);
        events.computeIfAbsent(tenantId, k -> new HashMap<>())
                .computeIfAbsent(event.entityId(), k -> new ArrayList<>())
                .add(storedEvent);
    }

    @Override
    public List<Event> getEvents(EntityId<?> entityId, TenantId tenantId, AccountId accountId) {
        List<StoredEvent> storedEvents = events.getOrDefault(tenantId, Map.of()).getOrDefault(entityId, List.of());
        return storedEvents.stream()
                .map(stored -> deserialize(encryption.decrypt(stored.encryptedData(), keyManager.getDataKey(accountId))))
                .toList();
    }

    @Override
    public void handle(Event event) {
        append(event, event.tenantId()); // Use event's TenantId
    }

    private byte[] serialize(Event event) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(event);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }

    private Event deserialize(byte[] data) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (Event) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to deserialize event", e);
        }
    }
}