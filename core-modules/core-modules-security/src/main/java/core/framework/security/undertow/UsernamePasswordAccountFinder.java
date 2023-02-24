package core.framework.security.undertow;

import io.undertow.security.idm.Account;

/**
 * @author ebin
 */
public interface UsernamePasswordAccountFinder {
    Account find(String username, String password);
}
