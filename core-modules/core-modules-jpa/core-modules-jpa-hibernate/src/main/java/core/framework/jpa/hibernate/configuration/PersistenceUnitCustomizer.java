package core.framework.jpa.hibernate.configuration;

import core.framework.jpa.hibernate.DomainEventTracking;
import core.framework.shared.utils.ResourcePatternResolverUtil;
import org.springframework.core.io.Resource;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.List;

/**
 * @author ebin
 */
public class PersistenceUnitCustomizer implements PersistenceUnitPostProcessor {
    private static final String XML_RESOURCE_PATTERN = "/**/*Finder.xml";

    @Override
    public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
        scanFinder(pui);
        pui.addManagedClassName(DomainEventTracking.class.getName());
    }

    private void scanFinder(MutablePersistenceUnitInfo pui) {
        List<String> managedPackages = pui.getManagedPackages();
        for (String pkg : managedPackages) {
            String pattern = ClassUtils.convertClassNameToResourcePath(pkg) + XML_RESOURCE_PATTERN;
            try {
                List<Resource> resources = ResourcePatternResolverUtil.resolve(pattern);
                for (Resource resource : resources) {
                    String url = resource.getURL().toString();
                    String basePackagePath = pkg.replace(".", "/").replace("*", "");
                    url = url.substring(url.indexOf(basePackagePath));
                    pui.addMappingFileName(url);
                }
            } catch (IOException e) {
                throw new Error("Failed to scan classpath for unlisted entity class mapping resources");
            }
        }
    }
}