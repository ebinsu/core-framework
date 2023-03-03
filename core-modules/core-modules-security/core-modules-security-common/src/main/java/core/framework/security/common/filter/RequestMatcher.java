package core.framework.security.common.filter;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author ebin
 */
@FunctionalInterface
public interface RequestMatcher {
    boolean matches(HttpServletRequest request);
}
