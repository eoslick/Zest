package com.ses.zest.event.adapters;

import com.ses.zest.event.ports.EventStore;
import com.ses.zest.common.*;
import com.ses.zest.encryption.ports.*;

import java.util.*;

public final class InMemoryEventStore implements EventStore {
    private final Map<TenantId, Map<EntityId<?>, List<Event>>> events = new HashMap<>();
    private final EncryptionAlgorithm encryption;
    private final KeyManager keyManager;

    public InMemoryEventStore(EncryptionAlgorithm encryption, KeyManager keyManager) {
        this.encryption = encryption;
        this.keyManager = keyManager;
    }

    @Override
    public void append(Event event, TenantId tenantId) {
        byte[] encryptedData = encryption.encrypt(serialize(event), keyManager.getDataKey(event.userId()));
        events.computeIfAbsent(tenantId, k -> new HashMap<>())
                .computeIfAbsent(event.entityId(), k -> new ArrayList<>())
                .add(event); // In practice, store encrypted form
    }

    @Override
    public List<Event> getEvents(EntityId<?> entityId, TenantId tenantId) {
        return List.copyOf(events.getOrDefault(tenantId, Map.of()).getOrDefault(entityId, List.of()));
    }

    private byte[] serialize(Event event) {
        // Placeholder: real impl would serialize to bytes
        return new byte[0];
    }
}