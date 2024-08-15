package core.framework.security.undertow.security;

import core.framework.security.undertow.authentication.mailecode.StringCredential;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.idm.PasswordCredential;

/**
 * @author ebin
 */
public class IdentityManagerImpl implements IdentityManager {
    private final AuthenticationRepository repository;

    public IdentityManagerImpl(AuthenticationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Account verify(Account account) {
        if (repository.verify(account)) {
            return account;
        }
        return null;
    }

    @Override
    public Account verify(String id, Credential credential) {
        String credentialStr = null;
        if (credential instanceof PasswordCredential passwordCredential) {
            credentialStr = new String(passwordCredential.getPassword());
        } else if (credential instanceof StringCredential stringCredential) {
            credentialStr = stringCredential.credentialStr();
        }
        if (credentialStr != null) {
            return repository.verify(id, credentialStr);
        }
        return null;
    }

    @Override
    public Account verify(Credential credential) {
        return null;
    }
}
