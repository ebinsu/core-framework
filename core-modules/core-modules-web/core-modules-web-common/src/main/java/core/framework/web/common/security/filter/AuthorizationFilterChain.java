package core.framework.web.common.security.filter;


import core.framework.web.common.security.SecurityContext;

import java.util.List;

/**
 * @author ebin
 */
public class AuthorizationFilterChain extends DefaultSecurityFilterChain {
    public AuthorizationFilterChain(List<String> patterns,
                                    List<String> excludePatterns,
                                    AuthorizationPermissionSupplier permissionSupplier,
                                    SecurityContext securityContext) {
        super(
                new AuthorizationRequestMatcher(patterns, excludePatterns),
                new AuthorizationFilter(permissionSupplier, securityContext)
        );
    }
}
