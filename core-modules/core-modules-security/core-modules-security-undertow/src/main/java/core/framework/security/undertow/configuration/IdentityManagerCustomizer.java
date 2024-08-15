package core.framework.security.undertow.configuration;

import core.framework.security.undertow.security.AuthenticationRepository;
import core.framework.security.undertow.security.IdentityManagerImpl;
import io.undertow.servlet.api.DeploymentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;

/**
 * @author ebin
 */
public class IdentityManagerCustomizer implements UndertowDeploymentInfoCustomizer {
    @Autowired
    private AuthenticationRepository repository;

    @Override
    public void customize(DeploymentInfo deploymentInfo) {
        IdentityManagerImpl identityManager = new IdentityManagerImpl(repository);
        deploymentInfo.setIdentityManager(identityManager);
    }
}
