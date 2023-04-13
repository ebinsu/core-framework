package core.framework.security.common.configuration;

import core.framework.security.common.SecurityType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author ebin
 */
@ConfigurationProperties(prefix = "spring.security")
public class SecurityProperties {
    private List<String> patterns = List.of();
    private List<String> excludePatterns = List.of();

    private SecurityLoginRequestProperties loginRequest = new SecurityLoginRequestProperties();
    private SecurityLogoutRequestProperties logoutRequest = new SecurityLogoutRequestProperties();

    private SecuritySessionProperties session;

    private SecurityType securityType = SecurityType.INTERCEPTOR;

    public List<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    public List<String> getExcludePatterns() {
        return excludePatterns;
    }

    public void setExcludePatterns(List<String> excludePatterns) {
        this.excludePatterns = excludePatterns;
    }

    public SecurityLoginRequestProperties getLoginRequest() {
        return loginRequest;
    }

    public void setLoginRequest(SecurityLoginRequestProperties loginRequest) {
        this.loginRequest = loginRequest;
    }

    public SecurityLogoutRequestProperties getLogoutRequest() {
        return logoutRequest;
    }

    public void setLogoutRequest(SecurityLogoutRequestProperties logoutRequest) {
        this.logoutRequest = logoutRequest;
    }

    public SecuritySessionProperties getSession() {
        return session;
    }

    public void setSession(SecuritySessionProperties session) {
        this.session = session;
    }

    public SecurityType getSecurityType() {
        return securityType;
    }

    public void setSecurityType(SecurityType securityType) {
        this.securityType = securityType;
    }
}
