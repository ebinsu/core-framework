package core.framework.security.undertow.configuration;

import core.framework.security.undertow.session.redis.HttpHeaderSessionConfigWrapper;
import io.undertow.servlet.api.DeploymentInfo;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;

/**
 * @author ebin
 */
public class SessionConfigCustomizer implements UndertowDeploymentInfoCustomizer {

    @Override
    public void customize(DeploymentInfo deploymentInfo) {
        deploymentInfo.setSessionConfigWrapper(new HttpHeaderSessionConfigWrapper());
    }
}
