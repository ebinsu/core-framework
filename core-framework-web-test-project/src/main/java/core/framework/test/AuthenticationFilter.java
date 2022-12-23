package core.framework.test;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME;

/**
 * @author ebin
 */
//@WebFilter(filterName = "authenticationFilter", servletNames = DEFAULT_DISPATCHER_SERVLET_BEAN_NAME, urlPatterns = "/*")
public class AuthenticationFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        boolean authenticationRequest = isAuthenticationRequest(req);
        if (authenticationRequest || !isAnonymous(req)) {
            if (!req.authenticate(res)) {
                return;
            }
        }
        super.doFilter(req, res, chain);
    }

    private boolean isAuthenticationRequest(HttpServletRequest req) {
        return req.getRequestURI().equals("/auth");
    }

    private boolean isAnonymous(HttpServletRequest req) {
        return PermissionMapping.anonymous.contains(req.getMethod().toUpperCase() + req.getRequestURI());
    }
}
