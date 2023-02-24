package core.framework.security.filter;

import org.springframework.http.HttpMethod;

/**
 * @author ebin
 */
public class LogoutFilterChain extends DefaultSecurityFilterChain {
    public LogoutFilterChain() {
        super(
                req -> req.getMethod().equals(HttpMethod.PUT.name()) && req.getRequestURI().equals("/logout"),
                new LogoutFilter()
        );
    }
}
