package core.framework.security.common.configuration;

import core.framework.security.common.AuthenticationType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author ebin
 */
@ConfigurationProperties(prefix = "spring.security")
public class SecurityAuthProperties {
    private List<String> authorizationPatterns = List.of();
    private AuthenticationType authenticationType;
    private String authenticationMethod = "PUT";
    private String authenticationUrl = "/login";
    private String logoutMethod = "PUT";
    private String logoutUrl = "/logout";

    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(AuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getAuthenticationUrl() {
        return authenticationUrl;
    }

    public void setAuthenticationUrl(String authenticationUrl) {
        this.authenticationUrl = authenticationUrl;
    }

    public String getAuthenticationMethod() {
        return authenticationMethod;
    }

    public void setAuthenticationMethod(String authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
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

    public List<String> getAuthorizationPatterns() {
        return authorizationPatterns;
    }

    public void setAuthorizationPatterns(List<String> authorizationPatterns) {
        this.authorizationPatterns = authorizationPatterns;
    }
}
