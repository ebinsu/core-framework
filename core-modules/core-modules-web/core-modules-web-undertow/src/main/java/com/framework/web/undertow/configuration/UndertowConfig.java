package com.framework.web.undertow.configuration;

import com.framework.web.undertow.security.AuthenticationRepository;
import core.framework.web.common.configuration.SecurityProperties;
import core.framework.web.common.security.filter.AuthorizationPermissionSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

/**
 * @author ebin
 */
@Configuration
public class UndertowConfig {
    @Bean
    public AuthenticationCustomizer ajaxAuthCustomizer(Environment environment,
                                                       SecurityProperties securityAuthProperties) {
        String applicationName = environment.getProperty("spring.application.name");
        return new AuthenticationCustomizer(applicationName, securityAuthProperties);
    }

    @Bean
    public IdentityManagerCustomizer identityManagerCustomizer(AuthenticationRepository repository) {
        return new IdentityManagerCustomizer(repository);
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
    public SecurityContextDeploymentInfoCustomizer securityContextDeploymentInfoCustomizer() {
        return new SecurityContextDeploymentInfoCustomizer();
    }
}

