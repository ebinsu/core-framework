package core.framework.security.common.interceptor;

import core.framework.security.common.annotation.Anonymous;
import core.framework.security.common.annotation.PermissionsRequired;
import core.framework.security.common.exception.ForbiddenException;
import core.framework.security.common.exception.UnauthorizedException;
import core.framework.security.common.filter.AuthorizationPermissionSupplier;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

/**
 * @author ebin
 */
public class SecurityHandlerInterceptor implements HandlerInterceptor {
    @Autowired
    private AuthorizationPermissionSupplier permissionSupplier;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            return true;
        }
        if (handler instanceof HandlerMethod handlerMethod) {
            if (!isAnonymous(handlerMethod)) {
                HttpSession session = request.getSession(false);
                if (session == null) {
                    throw new UnauthorizedException();
                }
                Set<String> permissions = permissionSupplier.getPermissions(session);
                if (!hasPermission(handlerMethod, permissions)) {
                    throw new ForbiddenException();
                }
            }
        }
        return true;
    }

    private static boolean isAnonymous(HandlerMethod handler) {
        Anonymous anonymous = handler.getMethod().getAnnotation(Anonymous.class);
        if (anonymous == null) {
            anonymous = handler.getBean().getClass().getAnnotation(Anonymous.class);
        }
        return anonymous != null;
    }

    private static boolean hasPermission(HandlerMethod handler, Set<String> permissions) {
        PermissionsRequired annotation = handler.getMethod().getAnnotation(PermissionsRequired.class);
        if (annotation == null) {
            annotation = handler.getBean().getClass().getAnnotation(PermissionsRequired.class);
        }
        if (annotation != null) {
            return permissions.contains(annotation.value());
        }
        return true;
    }
}
