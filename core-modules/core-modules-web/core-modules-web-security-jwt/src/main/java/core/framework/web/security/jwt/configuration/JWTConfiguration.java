package core.framework.web.security.jwt.configuration;

import core.framework.web.security.AuthStrategy;
import core.framework.web.security.jwt.JWTAuthStrategy;
import core.framework.web.security.jwt.configuration.properties.JWTProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
@EnableConfigurationProperties(JWTProperties.class)
public class JWTConfiguration {
    private final JWTProperties jwtProperties;

    public JWTConfiguration(JWTProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Bean
    public AuthStrategy jwtPrincipalStorage() {
        return new JWTAuthStrategy(jwtProperties);
    }
}
