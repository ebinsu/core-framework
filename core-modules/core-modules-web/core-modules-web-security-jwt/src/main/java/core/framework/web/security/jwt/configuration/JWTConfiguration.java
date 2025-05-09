package core.framework.web.security.jwt.configuration;

import core.framework.web.security.CurrentUserIdentityResolver;
import core.framework.web.security.CurrentUserRepository;
import core.framework.web.security.CurrentUserVerifyCustomize;
import core.framework.web.security.jwt.JWTCurrentUserIdentityResolver;
import core.framework.web.security.jwt.JWTCurrentUserRepository;
import core.framework.web.security.jwt.configuration.properties.JWTProperties;
import org.springframework.beans.factory.annotation.Autowired;
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
    public CurrentUserIdentityResolver jwtCurrentUserIdentityResolver() {
        return new JWTCurrentUserIdentityResolver();
    }

    @Bean
    public CurrentUserRepository jwtCurrentUserRepository(@Autowired Environment environment,
                                                          @Autowired(required = false) CurrentUserVerifyCustomize currentUserVerifyCustomizes) {
        return new JWTCurrentUserRepository(environment, jwtProperties, currentUserVerifyCustomizes);
    }
}
