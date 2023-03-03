package core.framework.security.undertow.identity;

import io.undertow.security.idm.IdentityManager;
import io.undertow.servlet.api.DeploymentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;

/**
 * @author ebin
 */
public class IdentityManagerCustomizer implements UndertowDeploymentInfoCustomizer {
    @Autowired
    private IdentityManager identityManager;

    @Override
    public void customize(DeploymentInfo deploymentInfo) {
        deploymentInfo.setIdentityManager(identityManager);
    }
}
