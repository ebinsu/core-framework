package core.framework.web.common.configuration;


import core.framework.web.common.configuration.properties.SecurityProperties;
import core.framework.web.common.security.filter.AuthorizationPermissionSupplier;
import core.framework.web.common.security.filter.EmptyAuthorizationPermissionSupplier;
import core.framework.web.common.security.interceptor.SecurityHandlerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
@ConditionalOnProperty(prefix = "spring.security", name = "enable")
public class SecurityConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public AuthorizationPermissionSupplier emptyAuthorizationPermissionSupplier() {
        return new EmptyAuthorizationPermissionSupplier();
    }

    @Bean
    public SecurityHandlerInterceptor securityHandlerInterceptor(AuthorizationPermissionSupplier permissionSupplier) {
        return new SecurityHandlerInterceptor(permissionSupplier);
    }
}
