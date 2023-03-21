package core.framework.security.undertow.authentication;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

/**
 * @author ebin
 */
public class SkipSecurityContextHandler implements HttpHandler {
    private final HttpHandler next;

    public SkipSecurityContextHandler(final HttpHandler next) {
        this.next = next;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.putAttachment(SkipSecurityContext.ATTACHMENT_KEY, Boolean.TRUE);
        next.handleRequest(exchange);
    }
}
