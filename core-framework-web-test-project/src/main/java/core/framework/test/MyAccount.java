package core.framework.test;

import com.sun.security.auth.UserPrincipal;
import io.undertow.security.idm.Account;

import java.security.Principal;
import java.util.Set;

/**
 * @author ebin
 */
public class MyAccount implements Account {
    public String name;
    public Set<String> roles;

    public MyAccount(String name, Set<String> roles) {
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
