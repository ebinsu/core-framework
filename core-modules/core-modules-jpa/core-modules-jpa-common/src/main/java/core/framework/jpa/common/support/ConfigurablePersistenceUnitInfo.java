package core.framework.jpa.common.support;

import jakarta.persistence.Converter;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.spi.ClassTransformer;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.index.CandidateComponentsIndex;
import org.springframework.context.index.CandidateComponentsIndexLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ebin
 */
public class ConfigurablePersistenceUnitInfo extends MutablePersistenceUnitInfo implements ResourceLoaderAware {
    private static final String CLASS_RESOURCE_PATTERN = "/**/*.class";
    private static final String XML_RESOURCE_PATTERN = "/**/*Finder.xml";
    private static final String PACKAGE_INFO_SUFFIX = ".package-info";
    private static final Set<AnnotationTypeFilter> ENTITY_TYPE_FILTERS;

    static {
        ENTITY_TYPE_FILTERS = new LinkedHashSet<>(8);
        ENTITY_TYPE_FILTERS.add(new AnnotationTypeFilter(Entity.class, false));
        ENTITY_TYPE_FILTERS.add(new AnnotationTypeFilter(Embeddable.class, false));
        ENTITY_TYPE_FILTERS.add(new AnnotationTypeFilter(MappedSuperclass.class, false));
        ENTITY_TYPE_FILTERS.add(new AnnotationTypeFilter(Converter.class, false));
    }

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private CandidateComponentsIndex componentsIndex;
    private ClassLoader classLoader = resourcePatternResolver.getClassLoader();

    public ConfigurablePersistenceUnitInfo(String persistenceUnitName) {
        this.setPersistenceUnitName(persistenceUnitName);
        this.setExcludeUnlistedClasses(true);
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    @Override
    public void addTransformer(ClassTransformer classTransformer) {
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.componentsIndex = CandidateComponentsIndexLoader.loadIndex(resourceLoader.getClassLoader());
    }

    public void setPackagesToScan(List<String> packagesToScan) {
        if (packagesToScan != null) {
            for (String pkg : packagesToScan) {
                scanPackage(pkg);
                scanMappingResources(pkg);
            }
        }
    }

    private void scanMappingResources(String pkg) {
        String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
            + ClassUtils.convertClassNameToResourcePath(pkg) + XML_RESOURCE_PATTERN;
        try {
            Resource[] resources = this.resourcePatternResolver.getResources(pattern);
            for (Resource resource : resources) {
                String url = resource.getURL().toString();
                String basePackagePath = pkg.replace(".", "/").replace("*", "");
                url = url.substring(url.indexOf(basePackagePath));
                this.addMappingFileName(url);
            }
        } catch (IOException e) {
            //ignore
            throw new PersistenceException("Failed to scan classpath for unlisted entity class mapping resources", e);
        }
    }

    private void scanPackage(String pkg) {
        if (this.componentsIndex != null) {
            Set<String> candidates = new HashSet<>();
            for (AnnotationTypeFilter filter : ENTITY_TYPE_FILTERS) {
                candidates.addAll(this.componentsIndex.getCandidateTypes(pkg, filter.getAnnotationType().getName()));
            }
            candidates.forEach(this::addManagedClassName);
            Set<String> managedPackages = this.componentsIndex.getCandidateTypes(pkg, "package-info");
            managedPackages.forEach(this::addManagedPackage);
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
                        this.addManagedClassName(className);
                        if (this.getPersistenceUnitRootUrl() == null) {
                            URL url = resource.getURL();
                            if (ResourceUtils.isJarURL(url)) {
                                this.setPersistenceUnitRootUrl(ResourceUtils.extractJarFileURL(url));
                            }
                        }
                    } else if (className.endsWith(PACKAGE_INFO_SUFFIX)) {
                        this.addManagedPackage(
                            className.substring(0, className.length() - PACKAGE_INFO_SUFFIX.length()));
                    }
                } catch (FileNotFoundException ex) {
                    // Ignore non-readable resource
                }
            }
        } catch (IOException ex) {
            throw new PersistenceException("Failed to scan classpath for unlisted entity classes", ex);
        }
    }

    private boolean matchesFilter(MetadataReader reader, MetadataReaderFactory readerFactory) throws IOException {
        for (TypeFilter filter : ENTITY_TYPE_FILTERS) {
            if (filter.match(reader, readerFactory)) {
                return true;
            }
        }
        return false;
    }
}
