package core.framework.security.configuration;

import core.framework.security.authorization.AuthorizationContextInitializer;
import core.framework.security.filter.AJAXAuthenticationFilterChain;
import core.framework.security.filter.FilterChainProxy;
import core.framework.security.filter.LogoutFilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
public class AuthorizationConfiguration {
    @Bean
    public AuthorizationContextInitializer authorizationContextInitializer() {
        return new AuthorizationContextInitializer();
    }

    @Bean
    public FilterChainProxy securityFilter() {
        FilterChainProxy filterChainProxy = new FilterChainProxy();
        filterChainProxy.addSecurityFilterChain(new AJAXAuthenticationFilterChain());
        filterChainProxy.addSecurityFilterChain(new LogoutFilterChain());
        return filterChainProxy;
    }
}
