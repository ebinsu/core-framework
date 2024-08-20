package com.framework.web.undertow.security.session.redis;

import com.framework.web.undertow.security.session.HttpHeaderSessionConfig;
import io.undertow.server.session.SessionConfig;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.SessionConfigWrapper;

/**
 * @author ebin
 */
public class HttpHeaderSessionConfigWrapper implements SessionConfigWrapper {
    @Override
    public SessionConfig wrap(SessionConfig sessionConfig, Deployment deployment) {
        return new HttpHeaderSessionConfig(sessionConfig);
    }
}
