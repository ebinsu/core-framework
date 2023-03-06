package core.framework.security.undertow.session;

import io.undertow.servlet.api.DeploymentInfo;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author ebin
 */
public class RedisSessionManagerCustomizer implements UndertowDeploymentInfoCustomizer {
    private final StringRedisTemplate redisTemplate;

    public RedisSessionManagerCustomizer(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void customize(DeploymentInfo deploymentInfo) {
        deploymentInfo.setSessionManagerFactory(new RedisSessionManagerFactory(redisTemplate));
    }
}
