package core.framework.test.web;

import core.framework.security.undertow.identity.DefaultAccount;
import core.framework.security.undertow.identity.UsernamePasswordAccountFinder;
import io.undertow.security.idm.Account;

import java.util.Set;

/**
 * @author ebin
 */
public class AccountFinder implements UsernamePasswordAccountFinder {
    @Override
    public Account find(String username, String password) {
        return new DefaultAccount(username, Set.of());
    }
}
