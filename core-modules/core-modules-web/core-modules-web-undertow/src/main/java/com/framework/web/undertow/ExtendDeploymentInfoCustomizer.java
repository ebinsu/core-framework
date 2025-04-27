package com.framework.web.undertow;

import com.framework.web.undertow.support.LogAttributeHandlerWrapper;
import io.undertow.servlet.api.DeploymentInfo;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;

/**
 * @author ebin
 */
public class ExtendDeploymentInfoCustomizer implements UndertowDeploymentInfoCustomizer {

    @Override
    public void customize(DeploymentInfo deploymentInfo) {
        deploymentInfo.addOuterHandlerChainWrapper(new LogAttributeHandlerWrapper());
    }
}
