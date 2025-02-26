package com.ses.zest.security.domain;

import com.ses.zest.user.domain.User;

public sealed interface AuthenticationResult {
    record Success(User user) implements AuthenticationResult {}
    record MFARequired(String method) implements AuthenticationResult {}
    record Failure(String reason) implements AuthenticationResult {}
}
