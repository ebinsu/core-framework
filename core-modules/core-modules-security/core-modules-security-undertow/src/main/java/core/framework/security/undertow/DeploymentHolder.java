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
    private static Deployment deployment;

    @Override
    public void handleDeployment(DeploymentInfo deploymentInfo, ServletContext servletContext) {
        if (deployment == null && servletContext instanceof ServletContextImpl impl) {
            deployment = impl.getDeployment();
        }
    }

    public static Deployment get() {
        return deployment;
    }
}
