package core.framework.security.common.filter;

/**
 * @author ebin
 */
public class AJAXAuthenticationFilterChain extends DefaultSecurityFilterChain {
    public AJAXAuthenticationFilterChain(String httpMethod, String uri) {
        super(
                req -> req.getMethod().equals(httpMethod) && req.getRequestURI().equals(uri),
                new AJAXAuthenticationFilter()
        );
    }
}
