package core.framework.security.undertow;

import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.LoginConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.core.env.Environment;

/**
 * @author ebin
 */
public class UndertowAJAXAuthenticationCustomizer implements UndertowDeploymentInfoCustomizer {
    @Autowired
    private Environment environment;

    @Override
    public void customize(DeploymentInfo deploymentInfo) {
        LoginConfig loginConfig = Servlets.loginConfig(environment.getProperty("spring.application.name"));
        loginConfig.addFirstAuthMethod(AJAXAuthenticationMechanism.NAME);
        deploymentInfo.setLoginConfig(loginConfig);
        deploymentInfo.addAuthenticationMechanism(AJAXAuthenticationMechanism.NAME, AJAXAuthenticationMechanism.FACTORY);
    }
}
