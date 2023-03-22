package core.framework.security.undertow.authorization;

import core.framework.security.common.filter.AuthorizationPermissionSupplier;
import core.framework.security.undertow.UndertowSessionConst;
import io.undertow.security.idm.Account;
import jakarta.servlet.http.HttpSession;

import java.util.Set;

/**
 * @author ebin
 */
public class AccountAuthorizationPermissionSupplier implements AuthorizationPermissionSupplier {
    @Override
    public Set<String> getPermissions(HttpSession session) {
        Account account = (Account) session.getAttribute(UndertowSessionConst.ACCOUNT);
        return account.getRoles();
    }
}
