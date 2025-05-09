package core.framework.web.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.Set;

/**
 * @author ebin
 */
public class SecurityHandlerInterceptor implements HandlerInterceptor {
    private final List<CurrentUserRepository> currentUserRepositories;
    private final SecurityHandlerMethodPreloader securityHandlerMethodPreloader;
    private final List<CurrentUserIdentityResolver> currentUserIdentityResolvers;

    public SecurityHandlerInterceptor(List<CurrentUserRepository> currentUserRepositories,
                                      SecurityHandlerMethodPreloader securityHandlerMethodPreloader,
                                      List<CurrentUserIdentityResolver> currentUserIdentityResolvers) {
        this.currentUserRepositories = currentUserRepositories;
        this.securityHandlerMethodPreloader = securityHandlerMethodPreloader;
        this.currentUserIdentityResolvers = currentUserIdentityResolvers;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            return true;
        }
        if (handler instanceof HandlerMethod handlerMethod) {
            if (!isAnonymous(handlerMethod)) {
                String currentUserIdentity = resolveCurrentUserIdentity(request);
                CurrentUser currentUser = loadCurrentUser(currentUserIdentity);
                if (currentUser == null) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    return false;
                }
                if (!hasPermission(handlerMethod, currentUser)) {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    return false;
                }
                RequestContextHolder.currentRequestAttributes().setAttribute(CurrentUserMethodArgumentResolver.REFERENCE_CURRENT_USER, currentUser, RequestAttributes.SCOPE_REQUEST);
            }
        }
        return true;
    }

    private CurrentUser loadCurrentUser(String currentUserIdentity) {
        for (CurrentUserRepository repository : currentUserRepositories) {
            CurrentUser currentUser = repository.load(currentUserIdentity);
            if (currentUser != null) {
                return currentUser;
            }
        }
        return null;
    }

    private String resolveCurrentUserIdentity(HttpServletRequest request) {
        for (CurrentUserIdentityResolver resolver : currentUserIdentityResolvers) {
            String id = resolver.resolve(request);
            if (id != null) {
                return id;
            }
        }
        return null;
    }

    private boolean isAnonymous(HandlerMethod handler) {
        return securityHandlerMethodPreloader.isMethodAnonymous(handler);
    }

    private boolean hasPermission(HandlerMethod handler, CurrentUser currentUser) {
        Set<String> permissionsRequired = securityHandlerMethodPreloader.getPermissionsRequired(handler);
        if (permissionsRequired != null) {
            return currentUser.hasPermission(permissionsRequired);
        }
        return true;
    }
}
