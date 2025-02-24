package com.ses.zest.user.domain;

import java.io.Serial;
import java.io.Serializable;

public record Email(String value) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public Email {
        if (!value.contains("@")) throw new IllegalArgumentException("Invalid email");
    }
}