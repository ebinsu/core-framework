package core.framework.web.security.configuration;

import core.framework.web.security.CurrentUserMethodArgumentResolver;
import core.framework.web.security.SecurityHandlerInterceptor;
import core.framework.web.security.configuration.properties.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author ebin
 */
@Configuration
public class SecurityWebMvcConfigurer implements WebMvcConfigurer {

    private final SecurityHandlerInterceptor securityHandlerInterceptor;
    private final SecurityProperties securityProperties;
    private final CurrentUserMethodArgumentResolver currentUserMethodArgumentResolver;

    public SecurityWebMvcConfigurer(SecurityProperties securityProperties,
                                    SecurityHandlerInterceptor securityHandlerInterceptor,
                                    CurrentUserMethodArgumentResolver currentUserMethodArgumentResolver) {
        this.securityProperties = securityProperties;
        this.securityHandlerInterceptor = securityHandlerInterceptor;
        this.currentUserMethodArgumentResolver = currentUserMethodArgumentResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(securityHandlerInterceptor);
        securityProperties.getPatterns().forEach(interceptorRegistration::addPathPatterns);
        securityProperties.getExcludePatterns().forEach(interceptorRegistration::excludePathPatterns);
    }


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.addLast(currentUserMethodArgumentResolver);
    }
}

