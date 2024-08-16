package core.framework.security.undertow.configuration;

import core.framework.security.undertow.ServletContextHolder;
import core.framework.security.undertow.security.SecurityContextFactoryImpl;
import io.undertow.servlet.api.DeploymentInfo;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;

/**
 * @author ebin
 */
public class SecurityContextDeploymentInfoCustomizer implements UndertowDeploymentInfoCustomizer {

    @Override
    public void customize(DeploymentInfo deploymentInfo) {
        deploymentInfo.addServletExtension(new ServletContextHolder());
        deploymentInfo.setSecurityContextFactory(new SecurityContextFactoryImpl());
    }
}
