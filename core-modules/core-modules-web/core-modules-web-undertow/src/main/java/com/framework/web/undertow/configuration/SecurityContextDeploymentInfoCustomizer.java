package com.framework.web.undertow.configuration;

import com.framework.web.undertow.security.SecurityContextFactoryImpl;
import com.framework.web.undertow.security.ServletContextHolder;
import com.framework.web.undertow.support.LogAttributeHandlerWrapper;
import io.undertow.servlet.api.DeploymentInfo;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;

/**
 * @author ebin
 */
public class SecurityContextDeploymentInfoCustomizer implements UndertowDeploymentInfoCustomizer {

    @Override
    public void customize(DeploymentInfo deploymentInfo) {
        deploymentInfo.addOuterHandlerChainWrapper(new LogAttributeHandlerWrapper());
        deploymentInfo.addServletExtension(new ServletContextHolder());
        deploymentInfo.setSecurityContextFactory(new SecurityContextFactoryImpl());
    }
}
