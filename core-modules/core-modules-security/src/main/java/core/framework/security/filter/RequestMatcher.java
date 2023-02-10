package core.framework.security.filter;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author ebin
 */
@FunctionalInterface
public interface RequestMatcher {
    boolean matches(HttpServletRequest request);
}
