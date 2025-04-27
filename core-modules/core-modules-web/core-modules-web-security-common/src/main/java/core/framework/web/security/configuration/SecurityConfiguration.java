package core.framework.web.security.configuration;

import core.framework.web.security.AuthStrategy;
import core.framework.web.security.SecurityHandlerInterceptor;
import core.framework.web.security.SecurityHandlerMethodPreloader;
import core.framework.web.security.configuration.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author ebin
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
@Import(SecurityWebMvcConfigurer.class)
public class SecurityConfiguration {

    @Bean
    public SecurityHandlerMethodPreloader securityHandlerMethodPreloader() {
        return new SecurityHandlerMethodPreloader();
    }

    @Bean
    public SecurityHandlerInterceptor securityHandlerInterceptor(@Autowired AuthStrategy authStrategy,
                                                                 @Autowired SecurityHandlerMethodPreloader securityHandlerMethodPreloader) {
        return new SecurityHandlerInterceptor(authStrategy, securityHandlerMethodPreloader);
    }
}
