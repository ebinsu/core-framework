package core.framework.web.common.security;

import core.framework.web.common.security.annotation.Anonymous;
import core.framework.web.common.security.annotation.PermissionsRequired;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Set;

/**
 * @author ebin
 */
public class SecurityContext {
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    public SecurityContext(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    public boolean isAnonymous(HttpServletRequest request) {
        try {
            HandlerExecutionChain chain = requestMappingHandlerMapping.getHandler(request);
            if (chain != null) {
                HandlerMethod handler = (HandlerMethod) chain.getHandler();
                return doValidAnonymous(handler);
            }
        } catch (Exception ignored) {

        }
        return false;
    }

    public boolean hasPermission(HttpServletRequest request, Set<String> permissions) {
        try {
            HandlerExecutionChain chain = requestMappingHandlerMapping.getHandler(request);
            if (chain != null) {
                HandlerMethod handler = (HandlerMethod) chain.getHandler();
                return doValidPermission(handler, permissions);
            } else {
                return true;
            }
        } catch (Exception ignored) {

        }
        return false;
    }

    private boolean doValidPermission(HandlerMethod handler, Set<String> permissions) {
        PermissionsRequired annotation = handler.getMethod().getAnnotation(PermissionsRequired.class);
        if (annotation == null) {
            annotation = handler.getBean().getClass().getAnnotation(PermissionsRequired.class);
        }
        if (annotation != null) {
            return permissions.contains(annotation.value());
        }
        return true;
    }

    private boolean doValidAnonymous(HandlerMethod handler) {
        Anonymous anonymous = handler.getMethod().getAnnotation(Anonymous.class);
        if (anonymous == null) {
            anonymous = handler.getBean().getClass().getAnnotation(Anonymous.class);
        }
        return anonymous != null;
    }
}
