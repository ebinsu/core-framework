package core.framework.security.common.filter;

import java.util.List;

/**
 * @author ebin
 */
public class AuthorizationFilterChain extends DefaultSecurityFilterChain {
    public AuthorizationFilterChain(List<String> patterns, AuthorizationPermissionSupplier permissionSupplier) {
        super(
                new AuthorizationRequestMatcher(patterns),
                new AuthorizationFilter(permissionSupplier)
        );
    }
}
