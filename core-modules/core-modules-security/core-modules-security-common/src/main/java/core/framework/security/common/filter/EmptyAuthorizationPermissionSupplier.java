package core.framework.security.common.filter;

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
