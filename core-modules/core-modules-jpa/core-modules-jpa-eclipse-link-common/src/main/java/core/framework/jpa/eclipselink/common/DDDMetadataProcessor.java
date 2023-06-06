package core.framework.jpa.eclipselink.common;

import jakarta.persistence.spi.PersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProcessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ConverterAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.sessions.AbstractSession;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ebin
 */
public class DDDMetadataProcessor extends MetadataProcessor {
    public DDDMetadataProcessor() {
    }

    public DDDMetadataProcessor(PersistenceUnitInfo puInfo, AbstractSession session, ClassLoader loader, boolean weaveLazy, boolean weaveEager, boolean weaveFetchGroups, boolean multitenantSharedEmf, boolean multitenantSharedCache, Map<String, Object> predeployProperties, MetadataProcessor compositeProcessor) {
        super(puInfo, session, loader, weaveLazy, weaveEager, weaveFetchGroups, multitenantSharedEmf, multitenantSharedCache, predeployProperties, compositeProcessor);
    }

    @Override
    protected void initPersistenceUnitClasses() {
        // 1 - Iterate through the classes that are defined in the <mapping>
        // files and add them to the map. This will merge the accessors where
        // necessary.
        HashMap<String, EntityAccessor> entities = new HashMap<String, EntityAccessor>();
        HashMap<String, EmbeddableAccessor> embeddables = new HashMap<String, EmbeddableAccessor>();

        for (XMLEntityMappings entityMappings : m_project.getEntityMappings()) {
            entityMappings.initPersistenceUnitClasses(entities, embeddables);
        }

        // 2 - Iterate through all the XML entities and add them to the project
        // and apply any persistence unit defaults.
        for (EntityAccessor entity : entities.values()) {
            // This will apply global persistence unit defaults.
            m_project.addEntityAccessor(entity);

            // This will override any global settings.
            entity.getEntityMappings().processEntityMappingsDefaults(entity);
        }

        // 3 - Iterate though all the XML embeddables and add them to the
        // project and apply any persistence unit defaults.
        for (EmbeddableAccessor embeddable : embeddables.values()) {
            // This will apply global persistence unit defaults.
            m_project.addEmbeddableAccessor(embeddable);

            // This will override any global settings.
            embeddable.getEntityMappings().processEntityMappingsDefaults(embeddable);
        }

        // Check if the xml-mapping-metadata-complete was declared.  If so, then ignore <class> entries defined
        // in the persistence unit, as by definition only elements declared in ORM XML are to be permitted.
        if (m_project.getPersistenceUnitMetadata() != null && m_project.getPersistenceUnitMetadata().isXMLMappingMetadataComplete()) {
            return;
        }

        // 4 - Iterate through the classes that are referenced from the
        // persistence.xml file.
        PersistenceUnitInfo persistenceUnitInfo = m_project.getPersistenceUnitInfo();
        List<String> classNames = new ArrayList<String>();

        // Add all the <class> specifications.
        classNames.addAll(persistenceUnitInfo.getManagedClassNames());

        // Add all the classes from the <jar> specifications.
        for (URL url : persistenceUnitInfo.getJarFileUrls()) {
            classNames.addAll(PersistenceUnitProcessor.getClassNamesFromURL(url, m_loader, null));
        }

        // Add all the classes off the classpath at the persistence unit root url.
        Set<String> unlistedClasses = Collections.emptySet();
        if (!persistenceUnitInfo.excludeUnlistedClasses()) {
            unlistedClasses = PersistenceUnitProcessor.getClassNamesFromURL(persistenceUnitInfo.getPersistenceUnitRootUrl(), m_loader, m_predeployProperties);
        }

        // 5 - Go through all the class names we found and add those classes
        // that have not yet been added. Be sure to check that the accessor
        // does not already exist since adding an accessor will merge its
        // contents with an existing accessor and we only want that to happen
        // in the XML case. Also, don't add an entity accessor if an embeddable
        // accessor to the same class exists (and vice versa). XML accessors
        // are loaded first, so preserve what we find there.
        Iterator<String> iterator = classNames.iterator();
        boolean unlisted = false;
        while (iterator.hasNext() || !unlisted) {
            if (!iterator.hasNext() && !unlisted) {
                iterator = unlistedClasses.iterator();
                unlisted = true;
            }
            if (iterator.hasNext()) {
                String className = iterator.next();
                MetadataClass candidateClass = m_factory.getMetadataClass(className, unlisted);
                // JBoss Bug 227630: Do not process a null class whether it was from a
                // NPE or a CNF, a warning or exception is thrown in loadClass()
                if (candidateClass != null) {
                    if (PersistenceUnitProcessor.isEntity(candidateClass) && !m_project.hasEntity(candidateClass) && !m_project.hasEmbeddable(candidateClass)) {
                        m_project.addEntityAccessor(new EntityAccessor(PersistenceUnitProcessor.getEntityAnnotation(candidateClass), candidateClass, m_project));
                    } else if (PersistenceUnitProcessor.isEmbeddable(candidateClass) && !m_project.hasEmbeddable(candidateClass) && !m_project.hasEntity(candidateClass)) {
                        m_project.addEmbeddableAccessor(new EmbeddableAccessor(PersistenceUnitProcessor.getEmbeddableAnnotation(candidateClass), candidateClass, m_project));
                    } else if (PersistenceUnitProcessor.isStaticMetamodelClass(candidateClass)) {
                        m_project.addStaticMetamodelClass(PersistenceUnitProcessor.getStaticMetamodelAnnotation(candidateClass), candidateClass);
                    } else if (PersistenceUnitProcessor.isConverter(candidateClass) && !m_project.hasConverterAccessor(candidateClass)) {
                        m_project.addConverterAccessor(new ConverterAccessor(PersistenceUnitProcessor.getConverterAnnotation(candidateClass), candidateClass, m_project));
                    } else if (PersistenceUnitProcessor.isMappedSuperclass(candidateClass) && !m_project.hasMappedSuperclass(candidateClass)) {
                        // ensure mapped superclasses will be added to the metamodel even if they do not have entity subclasses
                        // add the mapped superclass to keep track of it in case it is not processed later (has no subclasses).
                        m_project.addMappedSuperclass(new MappedSuperclassAccessor(
                            PersistenceUnitProcessor.getMappedSuperclassAnnotation(candidateClass),
                            candidateClass, m_project));
                    } else if ((DDDPersistenceUnitProcessor.isAggregateRoot(candidateClass) || DDDPersistenceUnitProcessor.isEntity(candidateClass))
                        && !m_project.hasEntity(candidateClass)
                        && !m_project.hasEmbeddable(candidateClass)) {
                        m_project.addEntityAccessor(new EntityAccessor(PersistenceUnitProcessor.getEntityAnnotation(candidateClass), candidateClass, m_project));
                    } else if (DDDPersistenceUnitProcessor.isValueObject(candidateClass) && !m_project.hasEmbeddable(candidateClass) && !m_project.hasEntity(candidateClass)) {
                        m_project.addEmbeddableAccessor(new EmbeddableAccessor(PersistenceUnitProcessor.getEmbeddableAnnotation(candidateClass), candidateClass, m_project));
                    }
                }
            }
        }
    }
}
