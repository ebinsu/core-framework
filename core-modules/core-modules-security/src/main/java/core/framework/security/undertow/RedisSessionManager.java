package core.framework.security.undertow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import core.framework.json.JSON;
import io.undertow.UndertowLogger;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.SecureRandomSessionIdGenerator;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionIdGenerator;
import io.undertow.server.session.SessionListener;
import io.undertow.server.session.SessionListeners;
import io.undertow.server.session.SessionManager;
import io.undertow.server.session.SessionManagerStatistics;
import io.undertow.util.AttachmentKey;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author ebin
 */
public class RedisSessionManager implements SessionManager {
    private final AttachmentKey<SessionImpl> NEW_SESSION = AttachmentKey.create(SessionImpl.class);

    private final String deploymentName;
    private final SessionIdGenerator sessionIdGenerator;
    private final SessionConfig sessionConfig;

    private int defaultSessionTimeout = 30 * 60;
    private final SessionListeners sessionListeners = new SessionListeners();
    private final StringRedisTemplate redisTemplate;

    public RedisSessionManager(String deploymentName, SessionConfig sessionCookieConfig, StringRedisTemplate redisTemplate) {
        this.deploymentName = deploymentName;
        this.sessionIdGenerator = new SecureRandomSessionIdGenerator();
        this.sessionConfig = sessionCookieConfig;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getDeploymentName() {
        return this.deploymentName;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public Session createSession(HttpServerExchange serverExchange, SessionConfig sessionConfig) {
        String sessionId = sessionConfig.findSessionId(serverExchange);
        if (sessionId == null) {
            sessionId = sessionIdGenerator.createSessionId();
        }

        SessionImpl session = new SessionImpl(sessionId, defaultSessionTimeout, this, this.sessionConfig);
        sessionConfig.setSessionId(serverExchange, session.getId());

        sessionListeners.sessionCreated(session, serverExchange);
        serverExchange.putAttachment(NEW_SESSION, session);
        return session;
    }

    @Override
    public Session getSession(HttpServerExchange serverExchange, SessionConfig sessionCookieConfig) {
        if (serverExchange != null) {
            SessionImpl newSession = serverExchange.getAttachment(NEW_SESSION);
            if (newSession != null) {
                return newSession;
            }
        } else {
            return null;
        }
        String sessionId = sessionCookieConfig.findSessionId(serverExchange);
        return getSession(sessionId);
    }

    @Override
    public Session getSession(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        String sessionJSON = redisTemplate.opsForValue().get(sessionId);
        if (sessionJSON == null) {
            return null;
        }
        SessionImpl session = JSON.fromJSON(SessionImpl.class, sessionJSON);
        session.redisSessionManager = this;
        session.sessionCookieConfig = sessionConfig;
        return session;
    }

    @Override
    public void registerSessionListener(SessionListener listener) {
        UndertowLogger.SESSION_LOGGER.debugf("Registered session listener %s", listener);
        sessionListeners.addSessionListener(listener);
    }

    @Override
    public void removeSessionListener(SessionListener listener) {
        UndertowLogger.SESSION_LOGGER.debugf("Removed session listener %s", listener);
        sessionListeners.removeSessionListener(listener);
    }

    @Override
    public void setDefaultSessionTimeout(int timeout) {
        UndertowLogger.SESSION_LOGGER.debugf("Setting default session timeout to %s", timeout);
        defaultSessionTimeout = timeout;
    }

    @Override
    public Set<String> getTransientSessions() {
        return Set.of();
    }

    @Override
    public Set<String> getActiveSessions() {
        return Set.of();
    }

    @Override
    public Set<String> getAllSessions() {
        return Set.of();
    }

    @Override
    public SessionManagerStatistics getStatistics() {
        throw new UnsupportedOperationException();
    }

    protected static class SessionImpl implements Session {
        private String sessionId;
        private final Map<String, Object> attributes = new HashMap<>();
        private long creationTime;
        private volatile int maxInactiveInterval;

        @JsonIgnore
        private RedisSessionManager redisSessionManager;
        @JsonIgnore
        private SessionConfig sessionCookieConfig;

        public SessionImpl() {
        }

        public SessionImpl(String sessionId, int maxInactiveInterval, RedisSessionManager redisSessionManager, SessionConfig sessionCookieConfig) {
            this.sessionId = sessionId;
            this.maxInactiveInterval = maxInactiveInterval;
            this.redisSessionManager = redisSessionManager;
            this.sessionCookieConfig = sessionCookieConfig;
            this.creationTime = ZonedDateTime.now().toEpochSecond();

            this.redisSessionManager.redisTemplate.opsForValue().set(sessionId, JSON.toJSON(this));
            this.bumpTimeout();
        }

        @Override
        public String getId() {
            return sessionId;
        }

        @Override
        public void requestDone(HttpServerExchange serverExchange) {

        }

        @Override
        public long getCreationTime() {
            return creationTime;
        }

        @Override
        public long getLastAccessedTime() {
            return 0;
        }

        @Override
        public void setMaxInactiveInterval(int interval) {
            this.maxInactiveInterval = interval;
            this.redisSessionManager.redisTemplate.opsForValue().set(sessionId, JSON.toJSON(this), maxInactiveInterval, TimeUnit.SECONDS);
        }

        @Override
        public int getMaxInactiveInterval() {
            return maxInactiveInterval;
        }

        @Override
        public Object getAttribute(String name) {
            return attributes.get(name);
        }

        @Override
        public Set<String> getAttributeNames() {
            return attributes.keySet();
        }

        @Override
        public Object setAttribute(String name, Object value) {
            Object put = attributes.put(name, value);
            this.redisSessionManager.redisTemplate.opsForValue().set(sessionId, JSON.toJSON(this), maxInactiveInterval, TimeUnit.SECONDS);
            this.redisSessionManager.sessionListeners.attributeAdded(this, name, value);
            return put;
        }

        @Override
        public Object removeAttribute(String name) {
            Object oldValue = attributes.remove(name);
            this.redisSessionManager.redisTemplate.opsForValue().set(sessionId, JSON.toJSON(this), maxInactiveInterval, TimeUnit.SECONDS);
            this.redisSessionManager.sessionListeners.attributeRemoved(this, name, oldValue);
            return oldValue;
        }

        @Override
        public void invalidate(HttpServerExchange exchange) {
            this.redisSessionManager.redisTemplate.delete(sessionId);
            if (exchange != null) {
                sessionCookieConfig.clearSession(exchange, this.getId());
            }
            redisSessionManager.sessionListeners.sessionDestroyed(this, exchange, SessionListener.SessionDestroyedReason.INVALIDATED);
        }

        @Override
        public SessionManager getSessionManager() {
            return this.redisSessionManager;
        }

        @Override
        public String changeSessionId(HttpServerExchange exchange, SessionConfig config) {
            synchronized (SessionImpl.this) {
                final String oldId = sessionId;
                if (exchange != null) {
                    config.clearSession(exchange, oldId);
                    config.setSessionId(exchange, this.getId());
                }

                String newId = redisSessionManager.sessionIdGenerator.createSessionId();
                this.sessionId = newId;

                this.redisSessionManager.redisTemplate.rename(oldId, newId);

                redisSessionManager.sessionListeners.sessionIdChanged(this, oldId);
                UndertowLogger.SESSION_LOGGER.debugf("Changing session id %s to %s", oldId, newId);
                return newId;
            }
        }

        private void bumpTimeout() {
            this.redisSessionManager.redisTemplate.opsForValue().getAndExpire(this.getId(), maxInactiveInterval, TimeUnit.SECONDS);
        }
    }
}
