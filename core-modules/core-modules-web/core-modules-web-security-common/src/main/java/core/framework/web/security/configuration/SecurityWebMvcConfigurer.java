package core.framework.web.security.configuration;

import core.framework.web.common.interceptor.InterceptorOrder;
import core.framework.web.security.SecurityHandlerInterceptor;
import core.framework.web.security.configuration.properties.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author ebin
 */
@Configuration
public class SecurityWebMvcConfigurer implements WebMvcConfigurer {

    private final SecurityHandlerInterceptor securityHandlerInterceptor;
    private final SecurityProperties securityProperties;

    public SecurityWebMvcConfigurer(SecurityProperties securityProperties,
                                    SecurityHandlerInterceptor securityHandlerInterceptor) {
        this.securityProperties = securityProperties;
        this.securityHandlerInterceptor = securityHandlerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(securityHandlerInterceptor).order(InterceptorOrder.LEVEL_2);
        securityProperties.getPatterns().forEach(interceptorRegistration::addPathPatterns);
        securityProperties.getExcludePatterns().forEach(interceptorRegistration::excludePathPatterns);
    }
}

