package core.framework.security.common;

import core.framework.security.common.annotation.Anonymous;
import core.framework.security.common.annotation.PermissionsRequired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @author ebin
 */
@Component
public class SecurityContextInitializer implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        RequestMappingHandlerMapping handlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        handlerMapping.getHandlerMethods().forEach((k, v) -> k.getMethodsCondition().getMethods().forEach(method -> {
            if (k.getPathPatternsCondition() != null) {
                k.getPathPatternsCondition().getPatterns().forEach(pathPattern -> {
                    if (hasAnonymous(v)) {
                        SecurityContext.addAnonymous(method.name() + "-" + pathPattern.getPatternString());
                    }

                    PermissionsRequired permissionsRequired = getPermissionsRequired(v);
                    if (permissionsRequired != null) {
                        SecurityContext.addPermission(method.name() + "-" + pathPattern.getPatternString(), permissionsRequired.value());
                    }
                });
            }
        }));
    }

    private PermissionsRequired getPermissionsRequired(HandlerMethod v) {
        PermissionsRequired annotation = v.getMethod().getAnnotation(PermissionsRequired.class);
        if (annotation == null) {
            annotation = v.getBean().getClass().getAnnotation(PermissionsRequired.class);
        }
        return annotation;
    }

    private boolean hasAnonymous(HandlerMethod v) {
        Anonymous anonymous = v.getMethod().getAnnotation(Anonymous.class);
        if (anonymous == null) {
            anonymous = v.getBean().getClass().getAnnotation(Anonymous.class);
        }
        return anonymous != null;
    }
}
