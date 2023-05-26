package core.framework.jpa.common.configuration;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;

import java.util.Objects;
import java.util.Set;

/**
 * @author ebin
 */
public class ExcludeAutoConfigurationImportFilter implements AutoConfigurationImportFilter {
    @Override
    public boolean[] match(String[] classNames, AutoConfigurationMetadata metadata) {
        boolean[] matches = new boolean[classNames.length];

        for (int i = 0; i < classNames.length; i++) {
            String className = classNames[i];
            if (Objects.nonNull(className)) {
                matches[i] = !(className.contains("org.springframework.boot.autoconfigure.data")
                    || className.contains("org.springframework.boot.autoconfigure.jdbc")
                    || className.contains("org.springframework.boot.autoconfigure.mongo"));
            }
        }
        return matches;
    }
}
