package com.ses.zest.security.domain;

public final class TOTPVerifier {
    public static boolean verify(String code, String secret) {
        // TODO: Implement TOTP verification using HMAC-SHA1 (RFC 6238)
        // For now, assume it works
        return true;
    }
}
