package core.framework.web.security.undertow.configuration;

import core.framework.web.security.CurrentUserIdentityResolver;
import core.framework.web.security.CurrentUserRepository;
import core.framework.web.security.CurrentUserVerifyCustomize;
import core.framework.web.security.undertow.ExtendUndertowDeploymentInfoCustomizer;
import core.framework.web.security.undertow.SessionCurrentUserIdentityResolver;
import core.framework.web.security.undertow.SessionCurrentUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public CurrentUserRepository sessionCurrentUserRepository(@Autowired(required = false) CurrentUserVerifyCustomize currentUserVerifyCustomizes) {
        return new SessionCurrentUserRepository(currentUserVerifyCustomizes);
    }

    @Bean
    public CurrentUserIdentityResolver sessionCurrentUserIdentityResolver() {
        return new SessionCurrentUserIdentityResolver();
    }
}
