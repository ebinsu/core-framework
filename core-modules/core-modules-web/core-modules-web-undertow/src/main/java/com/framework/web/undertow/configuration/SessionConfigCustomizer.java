package com.framework.web.undertow.configuration;

import com.framework.web.undertow.security.session.redis.HttpHeaderSessionConfigWrapper;
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
