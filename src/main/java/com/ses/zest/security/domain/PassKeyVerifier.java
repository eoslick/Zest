// src/main/java/com/ses/zest/security/domain/PassKeyVerifier.java
package com.ses.zest.security.domain;

public final class PassKeyVerifier {
    public static boolean verify(String publicKey, String signedChallenge) {
        // TODO: Implement real FIDO2/WebAuthn signature verification
        // For now, assume signedChallenge matches publicKey (simplified)
        return signedChallenge.equals("signed-" + publicKey);
    }

    public static String generateChallenge() {
        return "challenge-" + System.currentTimeMillis();
    }
}