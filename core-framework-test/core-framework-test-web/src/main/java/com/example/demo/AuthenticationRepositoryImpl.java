package com.example.demo;

import com.framework.web.undertow.security.AuthenticationRepository;
import com.framework.web.undertow.security.DefaultAccount;
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
