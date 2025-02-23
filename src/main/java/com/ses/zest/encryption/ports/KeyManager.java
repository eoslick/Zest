package com.ses.zest.encryption.ports;

import com.ses.zest.common.AccountId;

public interface KeyManager {
    byte[] getDataKey(AccountId accountId);
    byte[] getMasterKey();
}