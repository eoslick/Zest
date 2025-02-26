// src/main/java/com/ses/zest/user/application/CreateUser.java
package com.ses.zest.user.application;

import com.ses.zest.user.domain.*;
import com.ses.zest.user.ports.*;
import com.ses.zest.common.*;
import com.ses.zest.security.domain.Credentials;
import com.ses.zest.security.domain.PasswordHasher;
import com.ses.zest.security.domain.Role;
import com.ses.zest.security.ports.AuthenticationRepository;

public final class CreateUser {
    private final Users users;
    private final UserEvents userEvents;
    private final AuthenticationRepository authRepo;

    public CreateUser(Users users, UserEvents userEvents, AuthenticationRepository authRepo) {
        this.users = users;
        this.userEvents = userEvents;
        this.authRepo = authRepo;
    }

    public void execute(UserId id, TenantId tenantId, AccountId accountId, Email email, String password, Role role) {
        var user = new User(id, tenantId, accountId);
        user.create(email, role);
        String hashedPassword = PasswordHasher.hashPassword(password);
        authRepo.storeAuthenticationData(id, tenantId, new Credentials(hashedPassword, null, null, null));        users.save(user, tenantId);
        user.uncommittedEvents().forEach(userEvents::publish);
    }
}