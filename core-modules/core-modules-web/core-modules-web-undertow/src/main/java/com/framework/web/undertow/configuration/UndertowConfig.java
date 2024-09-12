package com.framework.web.undertow.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
public class UndertowConfig {
    @Bean
    public SessionConfigCustomizer sessionConfigCustomizer() {
        return new SessionConfigCustomizer();
    }

    @Bean
    public SecurityContextDeploymentInfoCustomizer securityContextDeploymentInfoCustomizer() {
        return new SecurityContextDeploymentInfoCustomizer();
    }
}

