package core.framework.web.common.configuration;


import core.framework.web.common.configuration.properties.SecurityProperties;
import core.framework.web.common.security.SecurityContext;
import core.framework.web.common.security.filter.AuthenticationFilterChain;
import core.framework.web.common.security.filter.AuthorizationFilterChain;
import core.framework.web.common.security.filter.AuthorizationPermissionSupplier;
import core.framework.web.common.security.filter.FilterChainProxy;
import core.framework.web.common.security.filter.LogoutFilterChain;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @author ebin
 */
@Configuration
@ConditionalOnProperty(prefix = "core.security", name = "security-type", havingValue = "FILTER")
public class SecurityFilterConfiguration {
    @Bean
    @ConditionalOnBean(CorsConfigurationSource.class)
    public CorsFilter corsFilter(CorsConfigurationSource configSource) {
        return new CorsFilter(configSource);
    }

    @Bean
    public SecurityContext securityContext(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        return new SecurityContext(requestMappingHandlerMapping);
    }

    @Bean
    public FilterChainProxy securityFilter(SecurityProperties securityProperties,
                                           AuthorizationPermissionSupplier authorizationPermissionSupplier,
                                           SecurityContext securityContext) {
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
