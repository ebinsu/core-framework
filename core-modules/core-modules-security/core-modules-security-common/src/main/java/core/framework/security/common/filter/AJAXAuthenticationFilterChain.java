package core.framework.security.common.filter;

import org.springframework.http.HttpMethod;

/**
 * @author ebin
 */
public class AJAXAuthenticationFilterChain extends DefaultSecurityFilterChain {
    public AJAXAuthenticationFilterChain() {
        super(
                req -> req.getMethod().equals(HttpMethod.PUT.name()) && req.getRequestURI().equals("/login"),
                new AJAXAuthenticationFilter()
        );
    }
}
