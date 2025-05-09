package core.framework.web.security;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author ebin
 */
public interface CurrentUserIdentityResolver {
    String resolve(HttpServletRequest request);
}
