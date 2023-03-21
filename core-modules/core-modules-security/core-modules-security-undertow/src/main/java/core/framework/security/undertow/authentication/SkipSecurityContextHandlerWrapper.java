package core.framework.security.undertow.authentication;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;

/**
 * @author ebin
 */
public class SkipSecurityContextHandlerWrapper implements HandlerWrapper {
    @Override
    public HttpHandler wrap(HttpHandler handler) {
        return new SkipSecurityContextHandler(handler);
    }
}
