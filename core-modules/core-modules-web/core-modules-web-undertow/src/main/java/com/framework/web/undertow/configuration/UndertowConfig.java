package com.framework.web.undertow.configuration;

import com.framework.web.undertow.ExtendDeploymentInfoCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
public class UndertowConfig {

    @Bean
    public ExtendDeploymentInfoCustomizer securityContextDeploymentInfoCustomizer() {
        return new ExtendDeploymentInfoCustomizer();
    }
}

