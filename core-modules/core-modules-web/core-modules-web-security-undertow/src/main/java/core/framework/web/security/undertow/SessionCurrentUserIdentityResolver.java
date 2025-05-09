package core.framework.web.security.undertow;

import core.framework.web.security.CurrentUserIdentityResolver;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.SessionConfig;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.servlet.spec.HttpServletRequestImpl;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author ebin
 */
public class SessionCurrentUserIdentityResolver implements CurrentUserIdentityResolver {
    @Override
    public String resolve(HttpServletRequest request) {
        HttpServerExchange exchange = ((HttpServletRequestImpl) request).getExchange();
        ServletRequestContext servletRequestContext = exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
        Deployment deployment = servletRequestContext.getDeployment();
        SessionConfig sessionConfig = deployment.getServletContext().getSessionConfig();
        return sessionConfig.findSessionId(exchange);
    }
}
