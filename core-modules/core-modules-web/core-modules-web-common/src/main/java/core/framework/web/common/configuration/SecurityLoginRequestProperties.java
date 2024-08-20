package core.framework.web.common.configuration;

import core.framework.web.common.security.AuthenticationType;

/**
 * @author ebin
 */
public class SecurityLoginRequestProperties {
    private AuthenticationType authenticationType = AuthenticationType.USERNAME_PASSWORD;
    private String method = "PUT";
    private String url = "/login";

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(AuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
    }
}
