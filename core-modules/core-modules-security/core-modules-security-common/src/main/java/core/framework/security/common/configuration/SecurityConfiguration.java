package core.framework.security.common.configuration;


import core.framework.security.common.SecurityContextInitializer;
import core.framework.security.common.filter.AuthenticationFilterChain;
import core.framework.security.common.filter.AuthorizationFilterChain;
import core.framework.security.common.filter.FilterChainProxy;
import core.framework.security.common.filter.LogoutFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
@EnableConfigurationProperties(SecurityAuthProperties.class)
public class SecurityConfiguration {
    @Autowired
    private SecurityAuthProperties securityAuthProperties;

    @Bean
    public SecurityContextInitializer securityContextInitializer() {
        return new SecurityContextInitializer();
    }

    @Bean
    public FilterChainProxy securityFilter() {
        FilterChainProxy filterChainProxy = new FilterChainProxy();
        filterChainProxy.addSecurityFilterChain(new AuthenticationFilterChain(securityAuthProperties.getAuthMethod(), securityAuthProperties.getAuthUrl()));
        filterChainProxy.addSecurityFilterChain(new LogoutFilterChain(securityAuthProperties.getLogoutMethod(), securityAuthProperties.getLogoutUrl()));
        filterChainProxy.addSecurityFilterChain(new AuthorizationFilterChain());
        return filterChainProxy;
    }
}
