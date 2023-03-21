package core.framework.security.undertow.authentication;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

/**
 * @author ebin
 */
public class ResetSkipSecurityContextHandler implements HttpHandler {
    private final HttpHandler next;

    public ResetSkipSecurityContextHandler(final HttpHandler next) {
        this.next = next;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.removeAttachment(SkipSecurityContext.ATTACHMENT_KEY);
        next.handleRequest(exchange);
    }
}
