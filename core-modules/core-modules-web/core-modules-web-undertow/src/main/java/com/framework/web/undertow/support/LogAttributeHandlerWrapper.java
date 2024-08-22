package com.framework.web.undertow.support;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;

/**
 * @author ebin
 */
public class LogAttributeHandlerWrapper implements HandlerWrapper {
    @Override
    public HttpHandler wrap(HttpHandler handler) {
        return new LogAttributeHttpHandler(handler);
    }
}
