package core.framework.security.undertow.session;

import io.undertow.server.session.SessionConfig;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.SessionConfigWrapper;

/**
 * @author ebin
 */
public class MySessionConfigWrapper implements SessionConfigWrapper {
    @Override
    public SessionConfig wrap(SessionConfig sessionConfig, Deployment deployment) {
        return new MySessionConfig(sessionConfig);
    }
}
