package core.framework.security.common.configuration;


import core.framework.security.common.interceptor.LoginOperation;
import core.framework.security.common.interceptor.LogoutOperation;
import core.framework.security.common.interceptor.SecurityWebMvcEndpointHandlerMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

import java.util.List;

/**
 * @author ebin
 */
@Configuration()
@ConditionalOnProperty(prefix = "spring.security", name = "security-type", havingValue = "INTERCEPTOR")
@Import(SecurityWebMvcConfigurer.class)
public class SecurityInterceptorConfiguration extends WebMvcConfigurationSupport {
    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private List<WebMvcConfigurer> webMvcConfigurers;

    @Bean
    public SecurityWebMvcEndpointHandlerMapping.ServletWebOperation loginOperation() {
        return new LoginOperation(
                securityProperties.getLoginRequest().getUrl(),
                RequestMethod.valueOf(securityProperties.getLoginRequest().getMethod())
        );
    }

    @Bean
    public SecurityWebMvcEndpointHandlerMapping.ServletWebOperation logoutOperation() {
        return new LogoutOperation(
                securityProperties.getLogoutRequest().getUrl(),
                RequestMethod.valueOf(securityProperties.getLogoutRequest().getMethod())
        );
    }

    @Bean
    public SecurityWebMvcEndpointHandlerMapping securityWebMvcEndpointHandlerMapping(@Autowired List<SecurityWebMvcEndpointHandlerMapping.ServletWebOperation> operations,
                                                                                     @Qualifier("mvcConversionService") FormattingConversionService conversionService,
                                                                                     @Qualifier("mvcResourceUrlProvider") ResourceUrlProvider resourceUrlProvider) {
        SecurityWebMvcEndpointHandlerMapping handlerMapping = new SecurityWebMvcEndpointHandlerMapping(operations);
        handlerMapping.setInterceptors(this.getInterceptors(conversionService, resourceUrlProvider));
        handlerMapping.setCorsConfigurations(this.getCorsConfigurations());
        return handlerMapping;
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        webMvcConfigurers.forEach(webMvcConfigurer -> webMvcConfigurer.addInterceptors(registry));
    }

    @Override
    protected void addCorsMappings(CorsRegistry registry) {
        webMvcConfigurers.forEach(webMvcConfigurer -> webMvcConfigurer.addCorsMappings(registry));
    }

}
