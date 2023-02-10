package core.framework.security.configuration;

import core.framework.security.authorization.SecurityContextInitializer;
import core.framework.security.filter.AJAXAuthenticationFilterChain;
import core.framework.security.filter.FilterChainProxy;
import core.framework.security.filter.LogoutFilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
public class SecurityConfiguration {
    @Bean
    public SecurityContextInitializer securityContextInitializer() {
        return new SecurityContextInitializer();
    }

    @Bean
    public FilterChainProxy securityFilter() {
        FilterChainProxy filterChainProxy = new FilterChainProxy();
        filterChainProxy.addSecurityFilterChain(new AJAXAuthenticationFilterChain());
        filterChainProxy.addSecurityFilterChain(new LogoutFilterChain());
        return filterChainProxy;
    }
}
