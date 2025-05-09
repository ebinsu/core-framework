package core.framework.web.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

/**
 * @author ebin
 */
public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver {
    public static final String REFERENCE_CURRENT_USER = "REFERENCE_CURRENT_USER";
    private final List<CurrentUserRepository> currentUserRepositories;
    private final List<CurrentUserIdentityResolver> currentUserIdentityResolvers;

    public CurrentUserMethodArgumentResolver(List<CurrentUserRepository> currentUserRepositories,
                                             List<CurrentUserIdentityResolver> currentUserIdentityResolvers) {
        this.currentUserRepositories = currentUserRepositories;
        this.currentUserIdentityResolvers = currentUserIdentityResolvers;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();
        return CurrentUser.class.isAssignableFrom(paramType);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Class<?> paramType = parameter.getParameterType();
        if (CurrentUser.class.isAssignableFrom(paramType)) {
            Object currentUser = RequestContextHolder.currentRequestAttributes().getAttribute(REFERENCE_CURRENT_USER, RequestAttributes.SCOPE_REQUEST);
            if (currentUser == null) {
                HttpServletRequest httpServletRequest = resolveNativeRequest(webRequest, HttpServletRequest.class);
                String id = resolveCurrentUserIdentity(httpServletRequest);
                currentUser = loadCurrentUser(id);
            }
            if (currentUser != null && !paramType.isInstance(currentUser)) {
                throw new IllegalStateException(
                    "Current user is not of type [" + paramType.getName() + "]: " + currentUser);
            }
            return currentUser;
        }
        throw new UnsupportedOperationException("Unknown parameter type: " + paramType.getName());
    }

    private <T> T resolveNativeRequest(NativeWebRequest webRequest, Class<T> requiredType) {
        T nativeRequest = webRequest.getNativeRequest(requiredType);
        if (nativeRequest == null) {
            throw new IllegalStateException(
                "Current request is not of type [" + requiredType.getName() + "]: " + webRequest);
        }
        return nativeRequest;
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

    private CurrentUser loadCurrentUser(String currentUserIdentity) {
        for (CurrentUserRepository repository : currentUserRepositories) {
            CurrentUser currentUser = repository.load(currentUserIdentity);
            if (currentUser != null) {
                return currentUser;
            }
        }
        return null;
    }
}
