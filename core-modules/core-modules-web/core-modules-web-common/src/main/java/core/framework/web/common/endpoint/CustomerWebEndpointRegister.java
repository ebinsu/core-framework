package core.framework.web.common.endpoint;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.Optional;

/**
 * @author ebin
 */
public class CustomerWebEndpointRegister implements InitializingBean {
    private final RequestMappingHandlerMapping handlerMapping;
    private final List<RouteProvider> routeProviders;

    public CustomerWebEndpointRegister(RequestMappingHandlerMapping handlerMapping, List<RouteProvider> routeProviders) {
        this.handlerMapping = handlerMapping;
        this.routeProviders = Optional.ofNullable(routeProviders).orElse(List.of());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<RouteDefinition> allRoutes = routeProviders.stream()
            .flatMap(provider -> provider.getRouteDefinitions().stream())
            .toList();
        allRoutes.forEach(this::registerRoute);
    }

    private void registerRoute(RouteDefinition routeDefinition) {
        try {
            RequestMappingInfo requestMappingInfo = RequestMappingInfo
                .paths(routeDefinition.path())
                .methods(routeDefinition.method())
                .build();
            assert RouterHandler.HANDLE_METHOD != null;
            handlerMapping.registerMapping(
                requestMappingInfo,
                new RouterHandler(routeDefinition),
                RouterHandler.HANDLE_METHOD
            );
        } catch (Exception e) {
            throw new IllegalStateException("Failed to register framework routeDefinition: " + routeDefinition.path(), e);
        }
    }
}
