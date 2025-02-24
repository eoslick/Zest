package com.ses.zest.common;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;


public abstract class EntityId<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID value;

    protected EntityId() {
        this.value = UUID.randomUUID();
    }

    protected EntityId(UUID value) {
        if (value == null) throw new IllegalArgumentException("ID cannot be null");
        this.value = value;
    }

    public UUID value() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityId<?> entityId = (EntityId<?>) o;
        return value.equals(entityId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}