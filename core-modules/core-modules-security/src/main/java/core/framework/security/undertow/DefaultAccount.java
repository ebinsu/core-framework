package core.framework.security.undertow;

import com.sun.security.auth.UserPrincipal;
import io.undertow.security.idm.Account;

import java.security.Principal;
import java.util.Set;

/**
 * @author ebin
 */
public class DefaultAccount implements Account {
    private final String name;
    private final Set<String> roles;

    public DefaultAccount(String name, Set<String> roles) {
        this.name = name;
        this.roles = roles;
    }

    @Override
    public Principal getPrincipal() {
        return new UserPrincipal(name);
    }

    @Override
    public Set<String> getRoles() {
        return roles;
    }
}

