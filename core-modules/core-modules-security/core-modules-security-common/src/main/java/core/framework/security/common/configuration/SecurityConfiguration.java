package core.framework.security.common.configuration;


import core.framework.security.common.SecurityContextInitializer;
import core.framework.security.common.filter.AJAXAuthenticationFilterChain;
import core.framework.security.common.filter.FilterChainProxy;
import core.framework.security.common.filter.LogoutFilterChain;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
@EnableConfigurationProperties(SecurityAuthProperties.class)
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
