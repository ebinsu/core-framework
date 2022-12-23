package core.framework.test;

import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.IdentityManager;

import java.util.Set;

/**
 * @author ebin
 */
public class MyIdentityManager implements IdentityManager {
    @Override
    public Account verify(Account account) {
        return null;
    }

    @Override
    public Account verify(String id, Credential credential) {
        if (id.equals("1")) {
            return new MyAccount(id, Set.of("manager"));
        }
        return null;
    }

    @Override
    public Account verify(Credential credential) {
        return null;
    }
}
