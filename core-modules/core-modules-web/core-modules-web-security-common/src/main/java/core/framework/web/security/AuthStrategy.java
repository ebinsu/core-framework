package core.framework.web.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author ebin
 */
public interface AuthStrategy {
    String authenticate(HttpServletRequest request, HttpServletResponse response, PrincipalDetail principalDetail);

    PrincipalDetail load(HttpServletRequest request);

    void destroy(HttpServletRequest request, HttpServletResponse response);
}
