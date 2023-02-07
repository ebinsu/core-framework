package core.framework.test.undertow;

import io.undertow.server.session.SessionManager;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.SessionManagerFactory;
import io.undertow.servlet.spec.SessionCookieConfigImpl;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.util.Properties;

/**
 * @author ebin
 */
public class RedisSessionManagerFactory implements SessionManagerFactory {
    private final String redisHost;
    private final int redisPort;
    private final int redisDB;

    public RedisSessionManagerFactory(String redisHost, int redisPort, int redisDB) {
        this.redisHost = redisHost;
        this.redisPort = redisPort;
        this.redisDB = redisDB;
    }

    @Override
    public SessionManager createSessionManager(Deployment deployment) {
        return new RedisSessionManager(
                deployment.getDeploymentInfo().getDeploymentName(),
                new SessionCookieConfigImpl(deployment.getServletContext()),
                redisHost,
                redisPort,
                redisDB);
    }
}
