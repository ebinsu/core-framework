package core.framework.security.undertow.configuration;

import core.framework.security.undertow.auth.AJAXAuthCustomizer;
import core.framework.security.undertow.identity.IdentityManagerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
public class UndertowConfig {
    @Bean
    public AJAXAuthCustomizer ajaxAuthCustomizer() {
        return new AJAXAuthCustomizer();
    }

    @Bean
    public IdentityManagerCustomizer identityManagerCustomizer() {
        return new IdentityManagerCustomizer();
    }
}

