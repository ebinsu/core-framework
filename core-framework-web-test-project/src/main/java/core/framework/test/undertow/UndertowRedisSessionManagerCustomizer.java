package core.framework.test.undertow;

import io.undertow.servlet.api.DeploymentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;

/**
 * @author ebin
 */
public class UndertowRedisSessionManagerCustomizer implements UndertowDeploymentInfoCustomizer {
    @Autowired
    private RedisSessionProperties redisSessionProperties;

    @Override
    public void customize(DeploymentInfo deploymentInfo) {
        deploymentInfo.setSessionManagerFactory(new RedisSessionManagerFactory(
                redisSessionProperties.getHost(),
                redisSessionProperties.getPort(),
                redisSessionProperties.getDb()
        ));
    }
}
