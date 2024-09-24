package com.framework.web.undertow.configuration;

import com.framework.web.undertow.security.AuthenticationRepository;
import core.framework.web.common.configuration.properties.SecurityProperties;
import core.framework.web.common.security.filter.AuthorizationPermissionSupplier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import java.util.Optional;

/**
 * @author ebin
 */
@Configuration
@ConditionalOnProperty(prefix = "core.security", name = "security-type")
public class UndertowSecurityConfig {
    @Bean
    public AuthenticationCustomizer ajaxAuthCustomizer(Environment environment,
                                                       SecurityProperties securityAuthProperties) {
        String applicationName = Optional.ofNullable(environment.getProperty("spring.application.name")).orElse("unknown");
        return new AuthenticationCustomizer(applicationName, securityAuthProperties);
    }

    @Bean
    public IdentityManagerCustomizer identityManagerCustomizer(AuthenticationRepository repository) {
        return new IdentityManagerCustomizer(repository);
    }

    @Bean
    @Primary
    public AuthorizationPermissionSupplier accountAuthorizationPermissionSupplier() {
        return new AccountAuthorizationPermissionSupplier();
    }
}
