package com.ses.zest.security.domain;

public interface Role {
    boolean canPerform(String action);
}