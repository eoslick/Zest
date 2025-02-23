package com.ses.zest.encryption.adapters;

import com.ses.zest.encryption.ports.EncryptionAlgorithm;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public final class AesEncryption implements EncryptionAlgorithm {
    private static final String ALGORITHM = "AES";

    @Override
    public byte[] encrypt(byte[] data, byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, ALGORITHM));
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] data, byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, ALGORITHM));
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }

    @Override
    public byte[] generateKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(256); // 256-bit AES
            return keyGen.generateKey().getEncoded();
        } catch (Exception e) {
            throw new RuntimeException("Key generation failed", e);
        }
    }
}