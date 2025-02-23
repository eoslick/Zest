package com.ses.zest.encryption.ports;

public interface EncryptionAlgorithm {
    byte[] encrypt(byte[] data, byte[] key);
    byte[] decrypt(byte[] data, byte[] key);
    byte[] generateKey();
}