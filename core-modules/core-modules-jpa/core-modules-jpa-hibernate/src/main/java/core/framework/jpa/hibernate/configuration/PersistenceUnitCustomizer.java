package core.framework.jpa.hibernate.configuration;

import core.framework.jpa.hibernate.DomainEventTracking;
import core.framework.shared.utils.ResourcePatternResolverUtil;
import core.framework.shared.utils.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ebin
 */
public class PersistenceUnitCustomizer implements PersistenceUnitPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceUnitCustomizer.class);
    private static final String XML_RESOURCE_PATTERN = "/**/*Finder.xml";

    @Override
    public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
        scanFinder(pui);
        pui.addManagedClassName(DomainEventTracking.class.getName());
    }

    private void scanFinder(MutablePersistenceUnitInfo pui) {
        StopWatch stopWatch = new StopWatch();
        List<String> managedPackages = pui.getManagedPackages();
        scanPackages(managedPackages, pui);

        Set<String> packages = pui.getManagedClassNames().stream().map(className -> {
            int index = className.lastIndexOf(".");
            if (index != -1) {
                return className.substring(0, index);
            } else {
                return className;
            }
        }).collect(Collectors.toSet());
        scanPackages(packages, pui);
        long elapsed = stopWatch.elapsed();
        LOGGER.info("Finished scan finder.xml in {} ms.", Duration.ofNanos(elapsed));
    }

    private void scanPackages(Collection<String> managedPackages, MutablePersistenceUnitInfo pui) {
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
                throw new Error("Failed to scan classpath for unlisted entity class mapping resources", e);
            }
        }
    }
}