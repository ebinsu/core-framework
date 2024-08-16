package core.framework.security.undertow.security;

import core.framework.security.undertow.ServletContextHolder;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.api.SecurityContextFactory;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.spec.ServletContextImpl;

/**
 * @author ebin
 */
public class SecurityContextFactoryImpl implements SecurityContextFactory {

    @Override
    public SecurityContext createSecurityContext(HttpServerExchange exchange, AuthenticationMode mode,
                                                 IdentityManager identityManager, String programmaticMechName) {
        ServletContextImpl servletContext = ServletContextHolder.servletContext();
        SessionSecurityContextImpl securityContext = new SessionSecurityContextImpl(exchange, mode, identityManager,
            servletContext.getSessionConfig(), servletContext.getDeployment().getSessionManager());
        if (programmaticMechName != null)
            securityContext.setProgramaticMechName(programmaticMechName);
        return securityContext;
    }
}
