package core.framework.security.undertow.security;

import io.undertow.security.idm.Account;

/**
 * @author ebin
 */
public interface AuthenticationRepository {
    default boolean verify(Account account) {
        return true;
    }

    Account verify(String id, String credential);
}
