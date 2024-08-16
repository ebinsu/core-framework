package core.framework.security.undertow;

import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.servlet.ServletExtension;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.spec.ServletContextImpl;
import jakarta.servlet.ServletContext;

/**
 * @author ebin
 */
public class ServletContextHolder implements ServletExtension {
    private static ServletContextImpl servletContext;

    @Override
    public void handleDeployment(DeploymentInfo deploymentInfo, ServletContext context) {
        if (servletContext == null && context instanceof ServletContextImpl impl) {
            servletContext = impl;
        }
    }

    public static SessionManager getSessionManager() {
        return servletContext.getDeployment().getSessionManager();
    }

    public static SessionConfig getSessionConfig() {
        return servletContext.getSessionConfig();
    }
}
