package com.ses.zest.security.domain;

import java.io.Serializable;

public record Credentials(String hashedPassword, String totpSecret, String passKeyPublicKey) implements Serializable {
    private static final long serialVersionUID = 1L;
}