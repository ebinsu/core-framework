package core.framework.web.security;

import core.framework.web.security.annotation.Anonymous;
import core.framework.web.security.annotation.PermissionsRequired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ebin
 */
public class SecurityHandlerMethodPreloader implements ApplicationListener<ContextRefreshedEvent> {
    private final Set<String> anonymousMethodCache = new HashSet<>();
    private final Map<String, Set<String>> permissionsRequiredMethodCache = new HashMap<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        var context = event.getApplicationContext();
        List<HandlerMethod> handlerMethods = context.getBeansOfType(RequestMappingHandlerMapping.class).values()
            .stream().flatMap(f -> f.getHandlerMethods().values().stream())
            .toList();

        handlerMethods.forEach(handlerMethod -> {
            Method method = handlerMethod.getMethod();
            Anonymous anonymousAnn = AnnotationUtils.findAnnotation(method, Anonymous.class);
            if (anonymousAnn != null) {
                anonymousMethodCache.add(handlerMethod.toString());
            }
            PermissionsRequired permissionsRequiredAnn = AnnotationUtils.findAnnotation(method, PermissionsRequired.class);
            if (permissionsRequiredAnn != null && permissionsRequiredAnn.values().length > 0) {
                permissionsRequiredMethodCache.put(handlerMethod.toString(), Arrays.stream(permissionsRequiredAnn.values()).collect(Collectors.toSet()));
            }
        });
    }

    public boolean isMethodAnonymous(HandlerMethod method) {
        return anonymousMethodCache.contains(method.toString());
    }

    public Set<String> getPermissionsRequired(HandlerMethod method) {
        return permissionsRequiredMethodCache.get(method.toString());
    }
}
