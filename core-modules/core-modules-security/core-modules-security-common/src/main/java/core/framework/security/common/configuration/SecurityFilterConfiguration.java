package core.framework.security.common.configuration;


import core.framework.security.common.SecurityContext;
import core.framework.security.common.filter.AuthenticationFilterChain;
import core.framework.security.common.filter.AuthorizationFilterChain;
import core.framework.security.common.filter.AuthorizationPermissionSupplier;
import core.framework.security.common.filter.FilterChainProxy;
import core.framework.security.common.filter.LogoutFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author ebin
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.security", name = "security-type", havingValue = "FILTER")
public class SecurityFilterConfiguration {
    @Autowired
    private SecurityProperties securityProperties;

    @Bean
    @ConditionalOnBean(CorsConfigurationSource.class)
    public CorsFilter corsFilter(CorsConfigurationSource configSource) {
        return new CorsFilter(configSource);
    }

    @Bean
    public SecurityContext securityContext() {
        return new SecurityContext();
    }

    @Bean
    public FilterChainProxy securityFilter(AuthorizationPermissionSupplier authorizationPermissionSupplier, SecurityContext securityContext) {
        FilterChainProxy filterChainProxy = new FilterChainProxy();
        filterChainProxy.addSecurityFilterChain(new AuthenticationFilterChain(securityProperties.getLoginRequest().getMethod(), securityProperties.getLoginRequest().getUrl()));
        filterChainProxy.addSecurityFilterChain(new LogoutFilterChain(securityProperties.getLogoutRequest().getMethod(), securityProperties.getLogoutRequest().getUrl()));
        filterChainProxy.addSecurityFilterChain(
                new AuthorizationFilterChain(securityProperties.getPatterns(),
                        securityProperties.getExcludePatterns(),
                        authorizationPermissionSupplier,
                        securityContext)
        );
        return filterChainProxy;
    }
}
