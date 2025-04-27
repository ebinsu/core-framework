package core.framework.web.common.endpoint;

import java.util.List;

/**
 * @author ebin
 */
public class DefaultRouteProvider implements RouteProvider {
    @Override
    public List<RouteDefinition> getRouteDefinitions() {
        return List.of(
            new HealthRouteDefinition()
        );
    }
}
