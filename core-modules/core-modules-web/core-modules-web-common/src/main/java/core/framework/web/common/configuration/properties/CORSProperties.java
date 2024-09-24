package core.framework.web.common.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author ebin
 */
@ConfigurationProperties(prefix = "core.web.cors")
public class CORSProperties {
    public List<CORSMapping> mappings;

    public List<CORSMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<CORSMapping> mappings) {
        this.mappings = mappings;
    }
}
