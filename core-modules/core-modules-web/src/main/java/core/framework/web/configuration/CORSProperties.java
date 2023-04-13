package core.framework.web.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author ebin
 */
@ConfigurationProperties(prefix = "spring.web.cors")
public class CORSProperties {
    public List<CORSMapping> mappings;

    public List<CORSMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<CORSMapping> mappings) {
        this.mappings = mappings;
    }
}
