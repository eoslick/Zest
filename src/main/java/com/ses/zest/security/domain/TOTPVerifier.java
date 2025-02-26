package com.ses.zest.security.domain;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

public final class TOTPVerifier {
    private static final int TIME_STEP = 30; // 30 seconds
    private static final int DIGITS = 6; // 6-digit code
    private static final String ALGORITHM = "HmacSHA1";

    public static String generateSecret() {
        byte[] secret = new byte[20]; // 160 bits
        new SecureRandom().nextBytes(secret);
        return Base64.getEncoder().encodeToString(secret);
    }

    public static boolean verify(String code, String base64Secret) {
        byte[] secret = Base64.getDecoder().decode(base64Secret);
        long timeStep = Instant.now().getEpochSecond() / TIME_STEP;
        String expectedCode = generateTOTP(secret, timeStep);
        return code.equals(expectedCode);
    }

    public static String generateCode(String base64Secret) { // Added public method
        byte[] secret = Base64.getDecoder().decode(base64Secret);
        long timeStep = Instant.now().getEpochSecond() / TIME_STEP;
        return generateTOTP(secret, timeStep);
    }

    private static String generateTOTP(byte[] secret, long timeStep) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.putLong(timeStep);
            byte[] timeBytes = buffer.array();

            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(secret, ALGORITHM));
            byte[] hash = mac.doFinal(timeBytes);

            int offset = hash[hash.length - 1] & 0xF;
            int binary = ((hash[offset] & 0x7F) << 24)
                    | ((hash[offset + 1] & 0xFF) << 16)
                    | ((hash[offset + 2] & 0xFF) << 8)
                    | (hash[offset + 3] & 0xFF);

            int otp = binary % 1000000;
            return String.format("%06d", otp);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate TOTP", e);
        }
    }
}