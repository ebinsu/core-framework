package core.framework.web.common.security.filter;

import jakarta.servlet.http.HttpSession;

import java.util.Set;

/**
 * @author ebin
 */
public interface AuthorizationPermissionSupplier {
    Set<String> getPermissions(HttpSession session);
}
