package core.framework.security.undertow.authentication;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;

/**
 * @author ebin
 */
public class ResetSkipSecurityContextHandlerWrapper implements HandlerWrapper {
    @Override
    public HttpHandler wrap(HttpHandler handler) {
        return new ResetSkipSecurityContextHandler(handler);
    }
}
