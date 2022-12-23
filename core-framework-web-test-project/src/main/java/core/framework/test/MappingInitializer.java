package core.framework.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Set;

/**
 * @author ebin
 */
@Component
public class MappingInitializer implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        RequestMappingHandlerMapping handlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        handlerMapping.getHandlerMethods().forEach((k, v) -> {
            k.getMethodsCondition().getMethods().forEach(method -> {
                if (k.getPathPatternsCondition() != null) {
                    k.getPathPatternsCondition().getPatterns().forEach(pathPattern -> {
                        PermissionsRequired annotation = v.getMethod().getAnnotation(PermissionsRequired.class);
                        if (annotation == null) {
                            annotation = v.getBean().getClass().getAnnotation(PermissionsRequired.class);
                        }
                        if (annotation != null) {
                            PermissionMapping.permissions.put(method.name() + pathPattern.getPatternString(), Set.of(annotation.value()));
                        }

                        Anonymous anonymous = v.getMethod().getAnnotation(Anonymous.class);
                        if (anonymous == null) {
                            anonymous = v.getBean().getClass().getAnnotation(Anonymous.class);
                        }
                        if (anonymous != null) {
                            PermissionMapping.anonymous.add(method.name() + pathPattern.getPatternString());
                        }
                    });
                }
            });
        });
    }
}
