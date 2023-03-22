package core.framework.security.common.configuration;


import core.framework.security.common.SecurityContextInitializer;
import core.framework.security.common.filter.AuthenticationFilterChain;
import core.framework.security.common.filter.AuthorizationFilterChain;
import core.framework.security.common.filter.AuthorizationPermissionSupplier;
import core.framework.security.common.filter.EmptyAuthorizationPermissionSupplier;
import core.framework.security.common.filter.FilterChainProxy;
import core.framework.security.common.filter.LogoutFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

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
    @ConditionalOnBean(CorsConfigurationSource.class)
    public CorsFilter corsFilter(CorsConfigurationSource configSource) {
        return new CorsFilter(configSource);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthorizationPermissionSupplier emptyAuthorizationPermissionSupplier() {
        return new EmptyAuthorizationPermissionSupplier();
    }

    @Bean
    public FilterChainProxy securityFilter(AuthorizationPermissionSupplier authorizationPermissionSupplier) {
        FilterChainProxy filterChainProxy = new FilterChainProxy();
        filterChainProxy.addSecurityFilterChain(new AuthenticationFilterChain(securityAuthProperties.getAuthenticationMethod(), securityAuthProperties.getAuthenticationUrl()));
        filterChainProxy.addSecurityFilterChain(new LogoutFilterChain(securityAuthProperties.getLogoutMethod(), securityAuthProperties.getLogoutUrl()));
        filterChainProxy.addSecurityFilterChain(new AuthorizationFilterChain(securityAuthProperties.getAuthorizationPatterns(), authorizationPermissionSupplier));
        return filterChainProxy;
    }
}
