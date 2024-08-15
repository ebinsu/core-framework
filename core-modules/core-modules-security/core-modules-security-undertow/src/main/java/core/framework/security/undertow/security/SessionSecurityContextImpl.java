package core.framework.security.undertow.security;

import io.undertow.security.api.AuthenticatedSessionManager;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.impl.SecurityContextImpl;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionManager;
import io.undertow.servlet.handlers.security.CachedAuthenticatedSessionHandler;

/**
 * @author ebin
 */
public class SessionSecurityContextImpl extends SecurityContextImpl {
    protected String sessionCookieName;
    protected SessionManager sessionManager;

    public SessionSecurityContextImpl(HttpServerExchange exchange, AuthenticationMode authenticationMode,
                                      IdentityManager identityManager, String sessionCookieName,
                                      SessionManager sessionManager) {
        super(exchange, authenticationMode, identityManager);
        Cookie requestCookie = exchange.getRequestCookie(sessionCookieName);
        if (requestCookie != null) {
            Session session = sessionManager.getSession(requestCookie.getValue());
            if (session != null) {
                AuthenticatedSessionManager.AuthenticatedSession authenticatedSession = (AuthenticatedSessionManager.AuthenticatedSession) session.getAttribute(CachedAuthenticatedSessionHandler.ATTRIBUTE_NAME);
                if (authenticatedSession != null) {
                    super.authenticationComplete(authenticatedSession.getAccount(), authenticatedSession.getMechanism(), false);
                }
            }
        }
    }
}
