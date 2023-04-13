package core.framework.security.common.configuration;

/**
 * @author ebin
 */
public class SecurityLogoutRequestProperties {
    private String method = "PUT";
    private String url = "/logout";

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
}
