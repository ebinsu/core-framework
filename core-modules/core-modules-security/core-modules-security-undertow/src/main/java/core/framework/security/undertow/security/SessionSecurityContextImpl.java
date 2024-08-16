package core.framework.security.undertow.security;

import io.undertow.security.api.AuthenticatedSessionManager;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.impl.SecurityContextImpl;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.servlet.handlers.security.CachedAuthenticatedSessionHandler;

import java.util.Optional;

/**
 * @author ebin
 */
public class SessionSecurityContextImpl extends SecurityContextImpl {

    public SessionSecurityContextImpl(HttpServerExchange exchange, AuthenticationMode authenticationMode,
                                      IdentityManager identityManager, SessionConfig sessionConfig,
                                      SessionManager sessionManager) {
        super(exchange, authenticationMode, identityManager);

        String sessionId = sessionConfig.findSessionId(exchange);
        Session session = Optional.ofNullable(sessionId)
            .map(sessionManager::getSession)
            .orElse(null);
        if (session != null) {
            AuthenticatedSessionManager.AuthenticatedSession authenticatedSession = (AuthenticatedSessionManager.AuthenticatedSession) session.getAttribute(CachedAuthenticatedSessionHandler.ATTRIBUTE_NAME);
            if (authenticatedSession != null) {
                super.authenticationComplete(authenticatedSession.getAccount(), authenticatedSession.getMechanism(), false);
            }
        }
    }
}
