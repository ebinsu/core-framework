package core.framework.security.common.configuration;

import core.framework.security.common.AuthType;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ebin
 */
@ConfigurationProperties(prefix = "spring.security.auth")
public class SecurityAuthProperties {
    private AuthType authType;
    private String authMethod = "PUT";
    private String authUrl = "/login";
    private String logoutMethod = "PUT";
    private String logoutUrl = "/logout";

    public AuthType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public String getAuthMethod() {
        return authMethod;
    }

    public void setAuthMethod(String authMethod) {
        this.authMethod = authMethod;
    }

    public String getLogoutMethod() {
        return logoutMethod;
    }

    public void setLogoutMethod(String logoutMethod) {
        this.logoutMethod = logoutMethod;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }
}
