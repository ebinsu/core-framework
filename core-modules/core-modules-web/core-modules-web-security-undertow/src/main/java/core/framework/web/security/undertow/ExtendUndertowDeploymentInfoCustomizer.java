package core.framework.web.security.undertow;

import core.framework.web.security.undertow.session.HttpHeaderSessionConfigWrapper;
import io.undertow.servlet.api.DeploymentInfo;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;

/**
 * @author ebin
 */
public class ExtendUndertowDeploymentInfoCustomizer implements UndertowDeploymentInfoCustomizer {
    @Override
    public void customize(DeploymentInfo deploymentInfo) {
        deploymentInfo.setSessionConfigWrapper(new HttpHeaderSessionConfigWrapper());
    }
}
