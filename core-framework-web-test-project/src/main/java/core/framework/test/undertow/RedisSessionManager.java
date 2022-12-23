package core.framework.test.undertow;

import framework.json.JSON;
import io.undertow.UndertowLogger;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionIdGenerator;
import io.undertow.server.session.SessionListener;
import io.undertow.server.session.SessionListeners;
import io.undertow.server.session.SessionManager;
import io.undertow.server.session.SessionManagerStatistics;
import io.undertow.util.AttachmentKey;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author ebin
 */
public class RedisSessionManager implements SessionManager {
    private final AttachmentKey<RedisSessionManager.SessionImpl> NEW_SESSION = AttachmentKey.create(RedisSessionManager.SessionImpl.class);

    private final SessionListeners sessionListeners = new SessionListeners();
    private String deploymentName;
    private SessionIdGenerator sessionIdGenerator;
    private SessionConfig sessionCookieConfig;
    private volatile int defaultSessionTimeout = 30 * 60;

    private RedisTemplate<String, String> redisTemplate;

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
        SessionImpl session = new SessionImpl();
        session.redisSessionManager = this;
        session.sessionCookieConfig = sessionConfig;
        if (sessionId == null) {
            sessionId = sessionIdGenerator.createSessionId();
        }
        session.setId(sessionId);
        sessionConfig.setSessionId(serverExchange, session.getId());
        redisTemplate.opsForValue().set(sessionId, JSON.toJSON(session), defaultSessionTimeout);
        sessionListeners.sessionCreated(session, serverExchange);
        serverExchange.putAttachment(NEW_SESSION, session);
        return session;
    }

    @Override
    public Session getSession(HttpServerExchange serverExchange, SessionConfig sessionCookieConfig) {
        if (serverExchange != null) {
            RedisSessionManager.SessionImpl newSession = serverExchange.getAttachment(NEW_SESSION);
            if (newSession != null) {
                return newSession;
            }
        } else {
            return null;
        }
        String sessionId = sessionCookieConfig.findSessionId(serverExchange);
        RedisSessionManager.SessionImpl session = (RedisSessionManager.SessionImpl) getSession(sessionId);
        if (session != null) {
            session.requestStarted(serverExchange);
        }
        return session;
    }

    @Override
    public Session getSession(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        String sessionStr = redisTemplate.opsForValue().get(sessionId);
        if (sessionStr == null) {
            return null;
        }
        return JSON.fromJSON(RedisSessionManager.SessionImpl.class, sessionStr);
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

    private static class SessionImpl implements Session {
        private String sessionId;
        private final Map<String, Object> attributes = new HashMap<>();

        private RedisSessionManager redisSessionManager;
        private SessionConfig sessionCookieConfig;

        @Override
        public String getId() {
            return sessionId;
        }

        public void setId(String sessionId) {
            this.sessionId = sessionId;
        }

        @Override
        public void requestDone(HttpServerExchange serverExchange) {

        }

        @Override
        public long getCreationTime() {
            return 0;
        }

        @Override
        public long getLastAccessedTime() {
            return 0;
        }

        @Override
        public void setMaxInactiveInterval(int interval) {

        }

        @Override
        public int getMaxInactiveInterval() {
            return 0;
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
            return attributes.put(name, value);
        }

        @Override
        public Object removeAttribute(String name) {
            return attributes.remove(name);
        }

        @Override
        public void invalidate(HttpServerExchange exchange) {
            if (exchange != null) {
                sessionCookieConfig.clearSession(exchange, this.getId());
            }
        }

        @Override
        public SessionManager getSessionManager() {
            return this.redisSessionManager;
        }

        @Override
        public String changeSessionId(HttpServerExchange exchange, SessionConfig config) {
            synchronized (RedisSessionManager.SessionImpl.this) {
                final String oldId = sessionId;
                String newId = redisSessionManager.sessionIdGenerator.createSessionId();
                this.sessionId = newId;
                config.setSessionId(exchange, this.getId());
//                sessionManager.sessions.remove(oldId);
                redisSessionManager.sessionListeners.sessionIdChanged(this, oldId);
                UndertowLogger.SESSION_LOGGER.debugf("Changing session id %s to %s", oldId, newId);
                return newId;
            }
        }

        public void requestStarted(HttpServerExchange serverExchange) {
            //todo refresh
        }
    }
}
