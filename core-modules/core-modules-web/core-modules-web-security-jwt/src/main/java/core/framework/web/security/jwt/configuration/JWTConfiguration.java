package core.framework.web.security.jwt.configuration;

import core.framework.web.security.AuthStrategy;
import core.framework.web.security.jwt.JWTAuthStrategy;
import core.framework.web.security.jwt.configuration.properties.JWTProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

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
    public AuthStrategy jwtPrincipalStorage(Environment environment) {
        return new JWTAuthStrategy(environment, jwtProperties);
    }
}
