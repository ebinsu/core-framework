package core.framework.web.common.endpoint;

import java.util.List;

/**
 * @author ebin
 */
@FunctionalInterface
public interface RouteProvider {
    List<RouteDefinition> getRouteDefinitions();
}
