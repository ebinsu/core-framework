package core.framework.security.undertow;

import io.undertow.server.session.SessionManager;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.SessionManagerFactory;
import io.undertow.servlet.spec.SessionCookieConfigImpl;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author ebin
 */
public class RedisSessionManagerFactory implements SessionManagerFactory {
    private final StringRedisTemplate redisTemplate;

    public RedisSessionManagerFactory(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public SessionManager createSessionManager(Deployment deployment) {
        return new RedisSessionManager(
                deployment.getDeploymentInfo().getDeploymentName(),
                new SessionCookieConfigImpl(deployment.getServletContext()),
                redisTemplate);
    }
}
