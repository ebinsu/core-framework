package core.framework.security.undertow.configuration;

import core.framework.security.common.configuration.SecuritySessionProperties;
import core.framework.security.undertow.DeploymentHolder;
import core.framework.security.undertow.security.SecurityContextFactoryImpl;
import io.undertow.servlet.api.DeploymentInfo;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;

/**
 * @author ebin
 */
public class SecurityContextDeploymentInfoCustomizer implements UndertowDeploymentInfoCustomizer {
    private final SecuritySessionProperties securitySessionProperties;

    public SecurityContextDeploymentInfoCustomizer(SecuritySessionProperties securitySessionProperties) {
        this.securitySessionProperties = securitySessionProperties;
    }

    @Override
    public void customize(DeploymentInfo deploymentInfo) {
        deploymentInfo.addServletExtension(new DeploymentHolder());
        SecurityContextFactoryImpl securityContextFactory = new SecurityContextFactoryImpl(securitySessionProperties.getName());
        deploymentInfo.setSecurityContextFactory(securityContextFactory);
    }
}
