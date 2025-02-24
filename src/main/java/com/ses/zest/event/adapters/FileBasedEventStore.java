package com.ses.zest.event.adapters;

import com.ses.zest.event.ports.*;
import com.ses.zest.common.*;
import com.ses.zest.encryption.ports.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class FileBasedEventStore implements EventStore, EventSubscriber {
    private final Path storageDir;
    private final EncryptionAlgorithm encryption;
    private final KeyManager keyManager;

    public FileBasedEventStore(Path storageDir, EncryptionAlgorithm encryption, KeyManager keyManager) {
        this.storageDir = storageDir;
        this.encryption = encryption;
        this.keyManager = keyManager;
        try {
            Files.createDirectories(storageDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create storage directory", e);
        }
    }

    @Override
    public void append(Event event, TenantId tenantId) {
        byte[] serializedEvent = serialize(event);
        byte[] encryptedData = encryption.encrypt(serializedEvent, keyManager.getDataKey(event.userId()));
        StoredEvent storedEvent = new StoredEvent(tenantId, encryptedData);
        Path dirPath = getDirPath(event.entityId(), tenantId);
        String fileName = event.timestamp().toEpochMilli() + "-" + UUID.randomUUID() + ".event";
        Path filePath = dirPath.resolve(fileName);
        try {
            Files.createDirectories(dirPath);
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                oos.writeObject(storedEvent);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to append event", e);
        }
    }

    @Override
    public List<Event> getEvents(EntityId<?> entityId, TenantId tenantId, AccountId accountId) {
        Path dirPath = getDirPath(entityId, tenantId);
        if (!Files.exists(dirPath)) return List.of();
        List<Event> events = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.event")) {
            for (Path filePath : stream) {
                try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))) {
                    StoredEvent stored = (StoredEvent) ois.readObject();
                    byte[] decryptedData = encryption.decrypt(stored.encryptedData(), keyManager.getDataKey(accountId));
                    events.add(deserialize(decryptedData));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read events", e);
        }
        return List.copyOf(events);
    }

    @Override
    public void handle(Event event) {
        append(event, event.tenantId());
    }

    private Path getDirPath(EntityId<?> entityId, TenantId tenantId) {
        return storageDir.resolve(tenantId.value().toString()).resolve(entityId.value().toString());
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