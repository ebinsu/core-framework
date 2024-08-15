package core.framework.security.undertow.security;

import core.framework.security.undertow.DeploymentHolder;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.api.SecurityContextFactory;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpServerExchange;

/**
 * @author ebin
 */
public class SecurityContextFactoryImpl implements SecurityContextFactory {
    protected String sessionCookieName;

    public SecurityContextFactoryImpl(String sessionCookieName) {
        this.sessionCookieName = sessionCookieName;
    }

    @Override
    public SecurityContext createSecurityContext(HttpServerExchange exchange, AuthenticationMode mode,
                                                 IdentityManager identityManager, String programmaticMechName) {
        SessionSecurityContextImpl securityContext = new SessionSecurityContextImpl(exchange, mode, identityManager,
            sessionCookieName, DeploymentHolder.get().getSessionManager());
        if (programmaticMechName != null)
            securityContext.setProgramaticMechName(programmaticMechName);
        return securityContext;
    }
}
