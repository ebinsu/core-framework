package core.framework.security.common.filter;

import core.framework.security.common.SecurityContextV2;

import java.util.List;

/**
 * @author ebin
 */
public class AuthorizationFilterChain extends DefaultSecurityFilterChain {
    public AuthorizationFilterChain(List<String> patterns, AuthorizationPermissionSupplier permissionSupplier, SecurityContextV2 securityContext) {
        super(
                new AuthorizationRequestMatcher(patterns),
                new AuthorizationFilter(permissionSupplier, securityContext)
        );
    }
}
