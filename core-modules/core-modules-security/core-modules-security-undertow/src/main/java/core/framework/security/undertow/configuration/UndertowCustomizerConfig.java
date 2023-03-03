package core.framework.security.undertow.configuration;

import core.framework.security.undertow.auth.AJAXAuthCustomizer;
import core.framework.security.undertow.identity.IdentityManagerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
public class UndertowCustomizerConfig {
    @Bean
    public AJAXAuthCustomizer undertowAJAXAuthenticationCustomizer() {
        return new AJAXAuthCustomizer();
    }

    @Bean
    public IdentityManagerCustomizer undertowIdentityManagerCustomizer() {
        return new IdentityManagerCustomizer();
    }
}

