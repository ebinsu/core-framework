package core.framework.test.web;

import core.framework.security.undertow.identity.DefaultAccount;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.IdentityManager;

import java.util.Set;

/**
 * @author ebin
 */
public class EmailCodeIdentityManager implements IdentityManager {
    @Override
    public Account verify(Account account) {
        return null;
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
