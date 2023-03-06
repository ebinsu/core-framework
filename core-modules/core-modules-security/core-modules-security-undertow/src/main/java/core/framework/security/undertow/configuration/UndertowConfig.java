package core.framework.security.undertow.configuration;

import core.framework.security.undertow.authentication.AuthenticationCustomizer;
import core.framework.security.undertow.identity.IdentityManagerCustomizer;
import core.framework.security.undertow.session.SessionConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}

