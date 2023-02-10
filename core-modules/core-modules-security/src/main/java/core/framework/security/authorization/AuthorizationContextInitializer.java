package core.framework.security.authorization;

import core.framework.security.annotation.Anonymous;
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
public class AuthorizationContextInitializer implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        RequestMappingHandlerMapping handlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        handlerMapping.getHandlerMethods().forEach((k, v) -> {
            k.getMethodsCondition().getMethods().forEach(method -> {
                if (k.getPathPatternsCondition() != null) {
                    k.getPathPatternsCondition().getPatterns().forEach(pathPattern -> {
                        if (hasAnonymous(v)) {
                            AuthorizationContext.addAnonymous(method.name() + "-" + pathPattern.getPatternString());
                        }
                    });
                }
            });
        });
    }

    private boolean hasAnonymous(HandlerMethod v) {
        Anonymous anonymous = v.getMethod().getAnnotation(Anonymous.class);
        if (anonymous == null) {
            anonymous = v.getBean().getClass().getAnnotation(Anonymous.class);
        }
        return anonymous != null;
    }
}
