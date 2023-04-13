package core.framework.security.common.configuration;


import core.framework.security.common.filter.AuthorizationPermissionSupplier;
import core.framework.security.common.filter.EmptyAuthorizationPermissionSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfiguration {
    @Autowired
    private SecurityProperties securityAuthProperties;

    @Bean
    @ConditionalOnMissingBean
    public AuthorizationPermissionSupplier emptyAuthorizationPermissionSupplier() {
        return new EmptyAuthorizationPermissionSupplier();
    }
}
