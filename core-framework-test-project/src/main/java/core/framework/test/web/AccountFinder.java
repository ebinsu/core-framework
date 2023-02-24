package core.framework.test.web;

import core.framework.security.undertow.DefaultAccount;
import io.undertow.security.idm.Account;

import java.util.Set;

/**
 * @author ebin
 */
public class AccountFinder implements core.framework.security.undertow.UsernamePasswordAccountFinder {
    @Override
    public Account find(String username, String password) {
        return new DefaultAccount(username, Set.of());
    }
}
