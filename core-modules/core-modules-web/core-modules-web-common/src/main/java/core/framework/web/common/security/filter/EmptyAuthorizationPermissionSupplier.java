package core.framework.web.common.security.filter;

import jakarta.servlet.http.HttpSession;

import java.util.Set;

/**
 * @author ebin
 */
public class EmptyAuthorizationPermissionSupplier implements AuthorizationPermissionSupplier {
    @Override
    public Set<String> getPermissions(HttpSession session) {
        return Set.of();
    }
}
