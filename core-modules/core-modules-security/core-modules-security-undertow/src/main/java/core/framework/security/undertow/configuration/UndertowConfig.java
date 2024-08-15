package core.framework.security.undertow.configuration;

import core.framework.security.common.configuration.SecurityProperties;
import core.framework.security.common.filter.AuthorizationPermissionSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author ebin
 */
@Configuration
public class UndertowConfig {
    @Bean
    public AuthenticationCustomizer ajaxAuthCustomizer() {
        return new AuthenticationCustomizer();
    }

    @Bean
    public IdentityManagerCustomizer identityManagerCustomizer() {
        return new IdentityManagerCustomizer();
    }

    @Bean
    public SessionConfigCustomizer sessionConfigCustomizer() {
        return new SessionConfigCustomizer();
    }

    @Bean
    @Primary
    public AuthorizationPermissionSupplier accountAuthorizationPermissionSupplier() {
        return new AccountAuthorizationPermissionSupplier();
    }

    @Bean
    public SecurityContextDeploymentInfoCustomizer securityContextDeploymentInfoCustomizer(SecurityProperties securityProperties) {
        return new SecurityContextDeploymentInfoCustomizer(securityProperties.getSession());
    }
}

