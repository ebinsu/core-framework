package core.framework.web.common.security.filter;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author ebin
 */
@FunctionalInterface
public interface RequestMatcher {
    boolean matches(HttpServletRequest request);
}
