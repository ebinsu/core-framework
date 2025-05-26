package com.framework.web.undertow.support;

import core.framework.kernel.log.Logs;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

/**
 * @author ebin
 */
public class LogAttributeHttpHandler implements HttpHandler {
    private final HttpHandler next;

    public LogAttributeHttpHandler(HttpHandler next) {
        this.next = next;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        long httpDelay = System.nanoTime() - exchange.getRequestStartTime();
        Logs.put("http_delay", httpDelay);
        next.handleRequest(exchange);
    }
}
