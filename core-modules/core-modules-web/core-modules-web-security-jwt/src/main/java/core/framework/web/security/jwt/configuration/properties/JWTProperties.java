package core.framework.web.security.jwt.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author ebin
 */
@ConfigurationProperties(prefix = "core.security.jwt")
public class JWTProperties {
    private String issuer;
    private String secret;
    private Long expirationSecond = Duration.ofHours(1).getSeconds();

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getExpirationSecond() {
        return expirationSecond;
    }

    public void setExpirationSecond(Long expirationSecond) {
        this.expirationSecond = expirationSecond;
    }
}
