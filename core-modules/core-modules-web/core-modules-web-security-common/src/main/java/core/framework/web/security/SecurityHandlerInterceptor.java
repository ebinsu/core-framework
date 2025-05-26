package core.framework.web.security;

import core.framework.kernel.log.marker.ErrorCodeMarker;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

/**
 * @author ebin
 */
public class SecurityHandlerInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityHandlerInterceptor.class);
    private final AuthStrategy authStrategy;
    private final SecurityHandlerMethodPreloader securityHandlerMethodPreloader;

    public SecurityHandlerInterceptor(AuthStrategy authStrategy, SecurityHandlerMethodPreloader securityHandlerMethodPreloader) {
        this.authStrategy = authStrategy;
        this.securityHandlerMethodPreloader = securityHandlerMethodPreloader;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            return true;
        }
        if (handler instanceof HandlerMethod handlerMethod) {
            if (!isAnonymous(handlerMethod)) {
                PrincipalDetail principalDetail = authStrategy.load(request);
                if (principalDetail == null) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    LOGGER.warn(new ErrorCodeMarker("UNAUTHORIZED"), "UNAUTHORIZED");
                    return false;
                }
                if (!hasPermission(handlerMethod, principalDetail)) {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    LOGGER.warn(new ErrorCodeMarker("FORBIDDEN"), "FORBIDDEN");
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isAnonymous(HandlerMethod handler) {
        return securityHandlerMethodPreloader.isMethodAnonymous(handler);
    }

    private boolean hasPermission(HandlerMethod handler, PrincipalDetail principalDetail) {
        Set<String> permissionsRequired = securityHandlerMethodPreloader.getPermissionsRequired(handler);
        if (permissionsRequired != null) {
            return principalDetail.hasPermission(permissionsRequired);
        }
        return true;
    }
}
