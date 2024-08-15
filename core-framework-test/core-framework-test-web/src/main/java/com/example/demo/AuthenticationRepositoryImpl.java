package com.example.demo;

import core.framework.security.undertow.security.AuthenticationRepository;
import core.framework.security.undertow.security.DefaultAccount;
import io.undertow.security.idm.Account;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author ebin
 */
@Repository
public class AuthenticationRepositoryImpl implements AuthenticationRepository {
    @Override
    public Account verify(String id, String credential) {
        return new DefaultAccount("xxx", Set.of());
    }
}
