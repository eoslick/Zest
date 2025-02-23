package com.ses.zest.encryption.adapters;

import com.ses.zest.encryption.ports.*;
import com.ses.zest.common.AccountId;

import java.util.HashMap;
import java.util.Map;

public final class InMemoryKeyManager implements KeyManager {
    private final Map<AccountId, byte[]> dataKeys = new HashMap<>();
    private final byte[] masterKey;

    public InMemoryKeyManager(EncryptionAlgorithm encryption) {
        this.masterKey = encryption.generateKey();
    }

    @Override
    public byte[] getDataKey(AccountId accountId) {
        return dataKeys.computeIfAbsent(accountId, k -> new AesEncryption().generateKey());
    }

    @Override
    public byte[] getMasterKey() {
        return masterKey;
    }
}