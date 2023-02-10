package core.framework.security.configuration;

import core.framework.security.undertow.SessionRedisTemplateSupplier;
import core.framework.security.undertow.UndertowAJAXAuthenticationCustomizer;
import core.framework.security.undertow.UndertowIdentityManagerCustomizer;
import core.framework.security.undertow.UndertowRedisSessionManagerCustomizer;
import core.framework.security.undertow.UsernamePasswordAccountFinder;
import core.framework.security.undertow.UsernamePasswordIdentityManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
public class UndertowCustomizerConfig {
    @Bean
    @ConditionalOnBean(SessionRedisTemplateSupplier.class)
    public UndertowDeploymentInfoCustomizer undertowRedisSessionManagerCustomizer(SessionRedisTemplateSupplier sessionRedisTemplateSupplier) {
        return new UndertowRedisSessionManagerCustomizer(sessionRedisTemplateSupplier.get());
    }

    @Bean
    public UndertowIdentityManagerCustomizer undertowIdentityManagerCustomizer() {
        return new UndertowIdentityManagerCustomizer();
    }

    @Bean
    public UndertowAJAXAuthenticationCustomizer undertowAJAXAuthenticationCustomizer() {
        return new UndertowAJAXAuthenticationCustomizer();
    }

    @Bean
    @ConditionalOnBean(UsernamePasswordAccountFinder.class)
    public UsernamePasswordIdentityManager usernamePasswordIdentityManager() {
        return new UsernamePasswordIdentityManager();
    }
}
