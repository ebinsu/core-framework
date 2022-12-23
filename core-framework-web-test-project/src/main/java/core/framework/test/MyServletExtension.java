package core.framework.test;

import core.framework.test.undertow.AJAXAuthenticationHttpHandler;
import core.framework.test.undertow.AJAXAuthenticationMechanism;
import io.undertow.servlet.ServletExtension;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.LoginConfig;
import jakarta.servlet.ServletContext;


/**
 * @author ebin
 */
public class MyServletExtension implements ServletExtension {

    @Override
    public void handleDeployment(DeploymentInfo deploymentInfo, ServletContext servletContext) {
        LoginConfig loginConfig = Servlets.loginConfig("test");
        loginConfig.addFirstAuthMethod(AJAXAuthenticationMechanism.NAME);
        deploymentInfo.setIdentityManager(new MyIdentityManager());
        deploymentInfo.setLoginConfig(loginConfig);

        deploymentInfo.addInnerHandlerChainWrapper(new AJAXAuthenticationHttpHandler.Wrapper());

        deploymentInfo.addAuthenticationMechanism(AJAXAuthenticationMechanism.NAME, AJAXAuthenticationMechanism.FACTORY);

//        deploymentInfo.addServlet(Servlets.servlet("authHttpServlet", AuthHttpServlet.class).addMapping("/auth"));
    }
}
