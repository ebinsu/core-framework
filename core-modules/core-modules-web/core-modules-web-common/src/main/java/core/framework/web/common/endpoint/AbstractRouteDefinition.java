package core.framework.web.common.endpoint;

import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author ebin
 */
public abstract class AbstractRouteDefinition implements RouteDefinition {
    private final String path;
    private final RequestMethod method;

    public AbstractRouteDefinition(String path, RequestMethod method) {
        this.path = path;
        this.method = method;
    }

    @Override
    public String path() {
        return this.path;
    }

    @Override
    public RequestMethod method() {
        return this.method;
    }
}
