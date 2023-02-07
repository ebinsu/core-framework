package core.framework.security.undertow;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ebin
 */
@ConfigurationProperties(prefix = "spring.session.redis")
public class RedisSessionProperties {
    private String host = "localhost";
    private Integer port = 0;
    private Integer db = 0;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getDb() {
        return db;
    }

    public void setDb(Integer db) {
        this.db = db;
    }
}
