package core.framework.web.security.undertow;

import core.framework.web.security.AbstractCurrentUserRepository;
import core.framework.web.security.CurrentUser;
import core.framework.web.security.CurrentUserVerifyCustomize;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.servlet.spec.HttpServletRequestImpl;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author ebin
 */
public class SessionCurrentUserRepository extends AbstractCurrentUserRepository {
    public SessionCurrentUserRepository(CurrentUserVerifyCustomize currentUserVerifyCustomize) {
        super(currentUserVerifyCustomize);
    }

    @Override
    protected String doSave(CurrentUser currentUser, Map<String, Object> currentUserMap) {
        HttpServerExchange exchange = serverExchange();
        ServletRequestContext servletRequestContext = exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
        Deployment deployment = servletRequestContext.getDeployment();
        SessionManager sessionManager = deployment.getSessionManager();
        SessionConfig sessionConfig = deployment.getServletContext().getSessionConfig();

        Session session = sessionManager.createSession(exchange, sessionConfig);
        currentUserMap.forEach(session::setAttribute);
        return session.getId();
    }

    @Override
    protected Map<String, Object> doLoad(String identity) {
        HttpServerExchange exchange = serverExchange();
        ServletRequestContext servletRequestContext = exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
        Deployment deployment = servletRequestContext.getDeployment();
        SessionManager sessionManager = deployment.getSessionManager();

        Session session = sessionManager.getSession(identity);
        if (session == null) {
            return null;
        } else {
            Set<String> attributeNames = session.getAttributeNames();
            Map<String, Object> map = new HashMap<>(attributeNames.size());
            attributeNames.forEach(name -> map.put(name, session.getAttribute(name)));
            return map;
        }
    }

    @Override
    public void destroy(String identity) {
        HttpServerExchange exchange = serverExchange();
        ServletRequestContext servletRequestContext = exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
        Deployment deployment = servletRequestContext.getDeployment();
        SessionManager sessionManager = deployment.getSessionManager();

        Session session = sessionManager.getSession(identity);
        if (session != null) {
            HttpServerExchange httpServerExchange = serverExchange();
            session.invalidate(httpServerExchange);
        }
    }

    private HttpServerExchange serverExchange() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return ((HttpServletRequestImpl) servletRequestAttributes.getRequest()).getExchange();
    }
}
