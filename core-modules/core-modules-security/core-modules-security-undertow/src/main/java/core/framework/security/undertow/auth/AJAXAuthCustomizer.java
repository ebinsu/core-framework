package core.framework.security.undertow.auth;

import core.framework.security.common.AuthType;
import core.framework.security.common.configuration.SecurityAuthProperties;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.LoginConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.core.env.Environment;

import java.util.Objects;

/**
 * @author ebin
 */
public class AJAXAuthCustomizer implements UndertowDeploymentInfoCustomizer {
    @Autowired
    private Environment environment;

    @Autowired
    private SecurityAuthProperties securityAuthProperties;

    @Override
    public void customize(DeploymentInfo deploymentInfo) {
        LoginConfig loginConfig = Servlets.loginConfig(environment.getProperty("spring.application.name"));
        AuthType authType = securityAuthProperties.getAuthType();
        if (Objects.requireNonNull(authType) == AuthType.EMAIL_CODE) {
            loginConfig.addFirstAuthMethod(EmailCodeAuthMechanism.NAME);
            deploymentInfo.addAuthenticationMechanism(EmailCodeAuthMechanism.NAME, EmailCodeAuthMechanism.FACTORY);
        } else {
            loginConfig.addFirstAuthMethod(UsernamePasswordAuthMechanism.NAME);
            deploymentInfo.addAuthenticationMechanism(UsernamePasswordAuthMechanism.NAME, UsernamePasswordAuthMechanism.FACTORY);
        }
    }
}
