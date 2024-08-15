package core.framework.security.undertow.configuration;

import core.framework.security.common.AuthenticationType;
import core.framework.security.common.configuration.SecurityProperties;
import core.framework.security.common.configuration.SecuritySessionProperties;
import core.framework.security.undertow.authentication.mailecode.EmailCodeAuthMechanism;
import core.framework.security.undertow.authentication.namepwd.UsernamePasswordAuthMechanism;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.AuthMethodConfig;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.LoginConfig;
import io.undertow.servlet.api.ServletSessionConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * @author ebin
 */
public class AuthenticationCustomizer implements UndertowDeploymentInfoCustomizer {
    public static final String AUTHENTICATION_METHOD = "AUTHENTICATION_METHOD";
    public static final String AUTHENTICATION_URL = "AUTHENTICATION_URL";

    @Autowired
    private Environment environment;

    @Autowired
    private SecurityProperties securityAuthProperties;

    @Override
    public void customize(DeploymentInfo deploymentInfo) {
        LoginConfig loginConfig = Servlets.loginConfig(environment.getProperty("spring.application.name"));
        deploymentInfo.setLoginConfig(loginConfig);
        AuthenticationType authType = securityAuthProperties.getLoginRequest().getAuthenticationType();
        Map<String, String> authProperties = Map.of(
            AUTHENTICATION_METHOD, securityAuthProperties.getLoginRequest().getMethod(),
            AUTHENTICATION_URL, securityAuthProperties.getLoginRequest().getUrl()
        );
        if (AuthenticationType.EMAIL_CODE == authType) {
            loginConfig.addFirstAuthMethod(new AuthMethodConfig(EmailCodeAuthMechanism.NAME, authProperties));
            deploymentInfo.addAuthenticationMechanism(EmailCodeAuthMechanism.NAME, EmailCodeAuthMechanism.FACTORY);
        } else {
            loginConfig.addFirstAuthMethod(new AuthMethodConfig(UsernamePasswordAuthMechanism.NAME, authProperties));
            deploymentInfo.addAuthenticationMechanism(UsernamePasswordAuthMechanism.NAME, UsernamePasswordAuthMechanism.FACTORY);
        }

        SecuritySessionProperties sessionProperties = securityAuthProperties.getSession();
        if (sessionProperties != null) {
            ServletSessionConfig servletSessionConfig = new ServletSessionConfig();
            servletSessionConfig.setName(sessionProperties.getName());
            servletSessionConfig.setPath(sessionProperties.getPath());
            servletSessionConfig.setDomain(sessionProperties.getDomain());
            servletSessionConfig.setSecure(sessionProperties.isSecure());
            servletSessionConfig.setHttpOnly(sessionProperties.isHttpOnly());
            servletSessionConfig.setMaxAge(sessionProperties.getMaxAge());

            deploymentInfo.setServletSessionConfig(servletSessionConfig);
        }
    }
}
