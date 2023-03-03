package core.framework.security.common.filter;

/**
 * @author ebin
 */
public class AuthenticationFilterChain extends DefaultSecurityFilterChain {
    public AuthenticationFilterChain(String httpMethod, String uri) {
        super(
                req -> req.getMethod().equals(httpMethod) && req.getRequestURI().equals(uri),
                new AuthenticationFilter()
        );
    }
}
