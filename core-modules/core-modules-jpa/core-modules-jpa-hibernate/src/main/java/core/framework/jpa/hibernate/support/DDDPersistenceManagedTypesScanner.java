package core.framework.jpa.hibernate.support;

import core.framework.ddd.annotation.AggregateRoot;
import core.framework.ddd.annotation.ValueObject;
import jakarta.persistence.Converter;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PersistenceException;
import org.springframework.context.index.CandidateComponentsIndex;
import org.springframework.context.index.CandidateComponentsIndexLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ebin
 */
public class DDDPersistenceManagedTypesScanner {
    private static final String CLASS_RESOURCE_PATTERN = "/**/*.class";

    private static final String PACKAGE_INFO_SUFFIX = ".package-info";

    private static final Set<AnnotationTypeFilter> ENTITY_TYPE_FILTERS = new LinkedHashSet<>(4);

    static {
        ENTITY_TYPE_FILTERS.add(new AnnotationTypeFilter(Entity.class, false));
        ENTITY_TYPE_FILTERS.add(new AnnotationTypeFilter(Embeddable.class, false));
        ENTITY_TYPE_FILTERS.add(new AnnotationTypeFilter(MappedSuperclass.class, false));
        ENTITY_TYPE_FILTERS.add(new AnnotationTypeFilter(Converter.class, false));
        ENTITY_TYPE_FILTERS.add(new AnnotationTypeFilter(AggregateRoot.class, false));
        ENTITY_TYPE_FILTERS.add(new AnnotationTypeFilter(core.framework.ddd.annotation.Entity.class, false));
        ENTITY_TYPE_FILTERS.add(new AnnotationTypeFilter(ValueObject.class, false));
    }

    private final ResourcePatternResolver resourcePatternResolver;

    @Nullable
    private final CandidateComponentsIndex componentsIndex;


    public DDDPersistenceManagedTypesScanner(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.componentsIndex = CandidateComponentsIndexLoader.loadIndex(resourceLoader.getClassLoader());
    }

    /**
     * Scan the specified packages and return a {@link PersistenceManagedTypes} that
     * represents the result of the scanning.
     *
     * @param packagesToScan the packages to scan
     * @return the {@link PersistenceManagedTypes} instance
     */
    public PersistenceManagedTypes scan(String... packagesToScan) {
        ScanResult scanResult = new ScanResult();
        for (String pkg : packagesToScan) {
            scanPackage(pkg, scanResult);
        }
        return scanResult.toJpaManagedTypes();
    }

    private void scanPackage(String pkg, ScanResult scanResult) {
        if (this.componentsIndex != null) {
            Set<String> candidates = new HashSet<>();
            for (AnnotationTypeFilter filter : ENTITY_TYPE_FILTERS) {
                candidates.addAll(this.componentsIndex.getCandidateTypes(pkg, filter.getAnnotationType().getName()));
            }
            scanResult.managedClassNames.addAll(candidates);
            scanResult.managedPackages.addAll(this.componentsIndex.getCandidateTypes(pkg, "package-info"));
            return;
        }

        try {
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + ClassUtils.convertClassNameToResourcePath(pkg) + CLASS_RESOURCE_PATTERN;
            Resource[] resources = this.resourcePatternResolver.getResources(pattern);
            MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
            for (Resource resource : resources) {
                try {
                    MetadataReader reader = readerFactory.getMetadataReader(resource);
                    String className = reader.getClassMetadata().getClassName();
                    if (matchesFilter(reader, readerFactory)) {
                        scanResult.managedClassNames.add(className);
                        if (scanResult.persistenceUnitRootUrl == null) {
                            URL url = resource.getURL();
                            if (ResourceUtils.isJarURL(url)) {
                                scanResult.persistenceUnitRootUrl = ResourceUtils.extractJarFileURL(url);
                            }
                        }
                    } else if (className.endsWith(PACKAGE_INFO_SUFFIX)) {
                        scanResult.managedPackages.add(className.substring(0,
                                className.length() - PACKAGE_INFO_SUFFIX.length()));
                    }
                } catch (FileNotFoundException ex) {
                    // Ignore non-readable resource
                    throw new Error("Failed to scan classpath for unlisted entity classes", ex);
                }
            }
        } catch (IOException ex) {
            throw new PersistenceException("Failed to scan classpath for unlisted entity classes", ex);
        }
    }

    /**
     * Check whether any of the configured entity type filters matches
     * the current class descriptor contained in the metadata reader.
     */
    private boolean matchesFilter(MetadataReader reader, MetadataReaderFactory readerFactory) throws IOException {
        for (TypeFilter filter : ENTITY_TYPE_FILTERS) {
            if (filter.match(reader, readerFactory)) {
                return true;
            }
        }
        return false;
    }

    public static class ScanResult {

        private final List<String> managedClassNames = new ArrayList<>();

        private final List<String> managedPackages = new ArrayList<>();

        @Nullable
        private URL persistenceUnitRootUrl;

        PersistenceManagedTypes toJpaManagedTypes() {
            return new SimplePersistenceManagedTypes(this.managedClassNames,
                    this.managedPackages, this.persistenceUnitRootUrl);
        }

    }

    public static class SimplePersistenceManagedTypes implements PersistenceManagedTypes {

        private final List<String> managedClassNames;

        private final List<String> managedPackages;

        @Nullable
        private final URL persistenceUnitRootUrl;


        SimplePersistenceManagedTypes(List<String> managedClassNames, List<String> managedPackages,
                                      @Nullable URL persistenceUnitRootUrl) {
            this.managedClassNames = managedClassNames;
            this.managedPackages = managedPackages;
            this.persistenceUnitRootUrl = persistenceUnitRootUrl;
        }

        @Override
        public List<String> getManagedClassNames() {
            return this.managedClassNames;
        }

        @Override
        public List<String> getManagedPackages() {
            return this.managedPackages;
        }

        @Override
        @Nullable
        public URL getPersistenceUnitRootUrl() {
            return this.persistenceUnitRootUrl;
        }

    }
}
