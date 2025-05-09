package core.framework.web.security.jwt;

import core.framework.web.security.CurrentUserIdentityResolver;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author ebin
 */
public class JWTCurrentUserIdentityResolver implements CurrentUserIdentityResolver {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public String resolve(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER);
        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return token.substring(BEARER_PREFIX_LENGTH);
    }
}
