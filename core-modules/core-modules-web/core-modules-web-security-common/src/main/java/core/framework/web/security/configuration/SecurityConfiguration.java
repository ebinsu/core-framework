package core.framework.web.security.configuration;

import core.framework.web.security.CurrentUserIdentityResolver;
import core.framework.web.security.CurrentUserMethodArgumentResolver;
import core.framework.web.security.CurrentUserRepository;
import core.framework.web.security.SecurityHandlerInterceptor;
import core.framework.web.security.SecurityHandlerMethodPreloader;
import core.framework.web.security.configuration.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

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
    public SecurityHandlerInterceptor securityHandlerInterceptor(@Autowired List<CurrentUserRepository> currentUserRepositories,
                                                                 @Autowired SecurityHandlerMethodPreloader securityHandlerMethodPreloader,
                                                                 @Autowired List<CurrentUserIdentityResolver> currentUserIdentityResolvers) {
        return new SecurityHandlerInterceptor(currentUserRepositories, securityHandlerMethodPreloader, currentUserIdentityResolvers);
    }

    @Bean
    public CurrentUserMethodArgumentResolver currentUserMethodArgumentResolver(@Autowired List<CurrentUserRepository> currentUserRepositories,
                                                                               @Autowired List<CurrentUserIdentityResolver> currentUserIdentityResolvers) {
        return new CurrentUserMethodArgumentResolver(currentUserRepositories, currentUserIdentityResolvers);
    }
}
