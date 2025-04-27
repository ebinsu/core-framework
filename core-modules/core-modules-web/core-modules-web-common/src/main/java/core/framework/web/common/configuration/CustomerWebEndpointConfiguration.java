package core.framework.web.common.configuration;

import core.framework.web.common.endpoint.CustomerWebEndpointRegister;
import core.framework.web.common.endpoint.DefaultRouteProvider;
import core.framework.web.common.endpoint.RouteProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

/**
 * @author ebin
 */
@Configuration
public class CustomerWebEndpointConfiguration {
    @Bean
    public RouteProvider routeProvider() {
        return new DefaultRouteProvider();
    }

    @Bean
    public CustomerWebEndpointRegister customerWebEndpointRegister(@Autowired @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping,
                                                                   @Autowired List<RouteProvider> routeProviders) {
        return new CustomerWebEndpointRegister(handlerMapping, routeProviders);
    }
}
