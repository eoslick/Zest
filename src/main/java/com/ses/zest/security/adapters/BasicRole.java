package com.ses.zest.security.adapters;

import com.ses.zest.security.domain.Role;

public enum BasicRole implements Role {
    SYSTEM_ADMIN {
        @Override
        public boolean canPerform(String action) {
            return true; // System admins can do anything
        }
    },
    TENANT_ADMIN {
        @Override
        public boolean canPerform(String action) {
            return !action.equals("system-wide"); // Tenant admins can’t perform system-wide actions
        }
    },
    USER {
        @Override
        public boolean canPerform(String action) {
            return action.equals("read-own-data"); // Users can only read their own data
        }
    }
}