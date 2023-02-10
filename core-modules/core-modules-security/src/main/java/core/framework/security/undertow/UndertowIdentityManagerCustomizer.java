package core.framework.security.undertow;

import io.undertow.security.idm.IdentityManager;
import io.undertow.servlet.api.DeploymentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;

/**
 * @author ebin
 */
public class UndertowIdentityManagerCustomizer implements UndertowDeploymentInfoCustomizer {
    @Autowired
    private IdentityManager identityManager;

    @Override
    public void customize(DeploymentInfo deploymentInfo) {
        deploymentInfo.setIdentityManager(identityManager);
    }
}
