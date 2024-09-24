package core.framework.web.common.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

/**
 * @author ebin
 */
@ConfigurationProperties(prefix = "core.security")
public class SecurityProperties {
    private boolean enable;
    private List<String> patterns = List.of();
    private List<String> excludePatterns = List.of();

    private SecurityLoginRequestProperties loginRequest = new SecurityLoginRequestProperties();
    private SecurityLogoutRequestProperties logoutRequest = new SecurityLogoutRequestProperties();

    private SecuritySessionProperties session = new SecuritySessionProperties();

    private SecurityType securityType = SecurityType.INTERCEPTOR;

    public List<String> getPatterns() {
        return Collections.unmodifiableList(patterns);
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    public List<String> getExcludePatterns() {
        return Collections.unmodifiableList(excludePatterns);
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

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
