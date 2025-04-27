package core.framework.web.security.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author ebin
 */
@ConfigurationProperties(prefix = "core.security")
public class SecurityProperties {
    private List<String> patterns = List.of("/**");
    private List<String> excludePatterns = List.of();

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
}
