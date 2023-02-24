package core.framework.security.undertow;

import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.idm.PasswordCredential;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author ebin
 */
public class UsernamePasswordIdentityManager implements IdentityManager {
    @Autowired
    private UsernamePasswordAccountFinder usernamePasswordAccountFinder;

    @Override
    public Account verify(Account account) {
        return null;
    }

    @Override
    public Account verify(String id, Credential credential) {
        if (credential instanceof PasswordCredential) {
            char[] password = ((PasswordCredential) credential).getPassword();
            return usernamePasswordAccountFinder.find(id, new String(password));
        }
        return null;
    }

    @Override
    public Account verify(Credential credential) {
        return null;
    }
}
