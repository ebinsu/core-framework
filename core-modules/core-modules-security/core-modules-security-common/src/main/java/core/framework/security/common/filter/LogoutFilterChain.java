package core.framework.security.common.filter;

/**
 * @author ebin
 */
public class LogoutFilterChain extends DefaultSecurityFilterChain {
    public LogoutFilterChain(String httpMethod, String uri) {
        super(
                req -> req.getMethod().equals(httpMethod) && req.getRequestURI().equals(uri),
                new LogoutFilter()
        );
    }
}
