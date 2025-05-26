package core.framework.web.common.configuration;

import core.framework.web.common.configuration.properties.CORSProperties;
import core.framework.web.common.exception.DefaultHandlerExceptionResolver;
import core.framework.web.common.exception.ExceptionHandlerCustomizer;
import core.framework.web.common.interceptor.InterceptorOrder;
import core.framework.web.common.interceptor.LogInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class DefaultWebMvcConfigurer implements WebMvcConfigurer {
    @Autowired
    private CORSProperties corsProperties;
    private final ObjectProvider<ExceptionHandlerCustomizer> exceptionHandlerCustomizers;

    public DefaultWebMvcConfigurer(ObjectProvider<ExceptionHandlerCustomizer> exceptionHandlerCustomizers) {
        this.exceptionHandlerCustomizers = exceptionHandlerCustomizers;
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        DefaultHandlerExceptionResolver defaultHandlerExceptionResolver = new DefaultHandlerExceptionResolver();
        exceptionHandlerCustomizers.orderedStream().forEach(exceptionHandlerCustomizer -> {
            exceptionHandlerCustomizer.exceptionHandlers().forEach(defaultHandlerExceptionResolver::addExceptionHandler);
        });
        resolvers.add(defaultHandlerExceptionResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (corsProperties.getMappings() != null) {
            corsProperties.getMappings().forEach(corsMapping ->
                registry.addMapping(corsMapping.getMapping())
                    .allowedOrigins(corsMapping.getAllowedOrigins().toArray(new String[]{}))
                    .allowedMethods(corsMapping.getAllowedMethods().toArray(new String[]{}))
                    .allowCredentials(corsMapping.isAllowCredentials())
            );
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor()).order(InterceptorOrder.LEVEL_1).addPathPatterns("/**");
    }
}
