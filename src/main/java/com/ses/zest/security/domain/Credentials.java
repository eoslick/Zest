package com.ses.zest.security.domain;

import java.io.Serial;
import java.io.Serializable;

public record Credentials(String hashedPassword, String totpSecret) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}