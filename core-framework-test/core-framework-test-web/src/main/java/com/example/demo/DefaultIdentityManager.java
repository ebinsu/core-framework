package com.example.demo;

import core.framework.security.undertow.security.DefaultAccount;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.IdentityManager;

import java.util.Set;

/**
 * @author ebin
 */
public class DefaultIdentityManager implements IdentityManager {
    @Override
    public Account verify(Account account) {
        return account;
    }

    @Override
    public Account verify(String id, Credential credential) {
        return new DefaultAccount("test", Set.of());
    }

    @Override
    public Account verify(Credential credential) {
        return null;
    }
}
