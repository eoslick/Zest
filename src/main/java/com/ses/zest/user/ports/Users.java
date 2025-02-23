package com.ses.zest.user.ports;

import com.ses.zest.common.*;
import com.ses.zest.user.domain.User;
import com.ses.zest.user.domain.UserId;

public interface Users {
    void save(User user, TenantId tenantId);
    User find(UserId id, TenantId tenantId); // Updated to UserId
}