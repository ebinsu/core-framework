package com.framework.web.undertow.support;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;

/**
 * @author ebin
 */
public class PerformanceStatHandlerWrapper implements HandlerWrapper {
    @Override
    public HttpHandler wrap(HttpHandler handler) {
        return new PerformanceStatHttpHandler(handler);
    }
}
