package com.framework.web.undertow.security.session;

import io.undertow.UndertowLogger;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionCookieConfig;

/**
 * @author ebin
 */
public class HttpHeaderSessionConfig implements SessionConfig {
    private final SessionConfig delegate;

    public HttpHeaderSessionConfig(SessionConfig delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setSessionId(HttpServerExchange exchange, String sessionId) {
        delegate.setSessionId(exchange, sessionId);
    }

    @Override
    public void clearSession(HttpServerExchange exchange, String sessionId) {
        delegate.clearSession(exchange, sessionId);
    }

    @Override
    public String findSessionId(HttpServerExchange exchange) {
        String sessionForCookie = delegate.findSessionId(exchange);
        if (sessionForCookie == null) {
            String session = exchange.getRequestHeaders().getFirst(SessionCookieConfig.DEFAULT_SESSION_ID);
            if (session != null) {
                UndertowLogger.SESSION_LOGGER.tracef("Found session id %s on %s", session, exchange);
                return session;
            }
            return null;
        }
        return sessionForCookie;
    }

    @Override
    public SessionCookieSource sessionCookieSource(HttpServerExchange exchange) {
        return delegate.sessionCookieSource(exchange);
    }

    @Override
    public String rewriteUrl(String originalUrl, String sessionId) {
        return delegate.rewriteUrl(originalUrl, sessionId);
    }
}
