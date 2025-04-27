package core.framework.web.security.undertow.configuration;

import core.framework.web.security.AuthStrategy;
import core.framework.web.security.session.SessionAuthStrategy;
import core.framework.web.security.undertow.ExtendUndertowDeploymentInfoCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
public class UndertowSecurityConfiguration {
    @Bean
    public UndertowDeploymentInfoCustomizer ajaxAuthCustomizer() {
        return new ExtendUndertowDeploymentInfoCustomizer();
    }

    @Bean
    public AuthStrategy sessionAuthStrategy() {
        return new SessionAuthStrategy();
    }
}
