package core.framework.security.common.filter;

/**
 * @author ebin
 */
public class AuthorizationFilterChain extends DefaultSecurityFilterChain {
    public AuthorizationFilterChain() {
        super(
                req -> true,
                new AuthorizationFilter()
        );
    }
}
