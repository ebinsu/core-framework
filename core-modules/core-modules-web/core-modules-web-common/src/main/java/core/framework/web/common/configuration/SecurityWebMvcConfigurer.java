package core.framework.web.common.configuration;

import core.framework.web.common.configuration.properties.SecurityProperties;
import core.framework.web.common.security.interceptor.SecurityHandlerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author ebin
 */
@Configuration
@ConditionalOnProperty(prefix = "core.security", name = "security-type", havingValue = "INTERCEPTOR")
public class SecurityWebMvcConfigurer implements WebMvcConfigurer {

    private final SecurityProperties securityProperties;
    private final SecurityHandlerInterceptor securityHandlerInterceptor;

    public SecurityWebMvcConfigurer(SecurityProperties securityProperties,
                                    SecurityHandlerInterceptor securityHandlerInterceptor) {
        this.securityProperties = securityProperties;
        this.securityHandlerInterceptor = securityHandlerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(securityHandlerInterceptor);
        securityProperties.getPatterns().forEach(interceptorRegistration::addPathPatterns);
        securityProperties.getExcludePatterns().forEach(interceptorRegistration::excludePathPatterns);
        interceptorRegistration.excludePathPatterns(securityProperties.getLoginRequest().getUrl());
    }
}
