package core.framework.web.security;

import org.springframework.web.context.request.RequestContextHolder;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

/**
 * @author ebin
 */
public abstract class SecurityContext {
    public static final String AUTHENTICATED_PRINCIPAL_KEY = "Security.AUTHENTICATED_PRINCIPAL_KEY";

    public static PrincipalDetail getCurrentPrincipal() {
        return (PrincipalDetail) RequestContextHolder.getRequestAttributes().getAttribute(AUTHENTICATED_PRINCIPAL_KEY, SCOPE_REQUEST);
    }

    protected static void setCurrentPrincipal(PrincipalDetail principalDetail) {
        RequestContextHolder.currentRequestAttributes().setAttribute(AUTHENTICATED_PRINCIPAL_KEY, principalDetail, SCOPE_REQUEST);
    }
}
