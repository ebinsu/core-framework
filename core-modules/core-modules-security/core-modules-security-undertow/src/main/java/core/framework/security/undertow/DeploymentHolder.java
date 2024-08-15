package core.framework.security.undertow;

import io.undertow.servlet.ServletExtension;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.spec.ServletContextImpl;
import jakarta.servlet.ServletContext;

/**
 * @author ebin
 */
public class DeploymentHolder implements ServletExtension {
    private static Deployment DEPLOYMENT;

    @Override
    public void handleDeployment(DeploymentInfo deploymentInfo, ServletContext servletContext) {
        if (DEPLOYMENT == null && servletContext instanceof ServletContextImpl impl) {
            DEPLOYMENT = impl.getDeployment();
        }
    }

    public static Deployment get() {
        return DEPLOYMENT;
    }
}
