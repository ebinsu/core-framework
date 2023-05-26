package core.framework.jpa.eclipselink;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceUnitInfo;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.SystemProperties;
import org.eclipse.persistence.exceptions.PersistenceUnitLoadingException;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.internal.jpa.deployment.JavaSECMPInitializer;
import org.eclipse.persistence.jpa.PersistenceProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Get PersistenceUnitName from ConfigurablePersistenceUnitInfo rather than persistence.xml
 *
 * @author ebin
 */
public class ConfigurablePersistenceUnitInfoPersistenceProvider extends PersistenceProvider {

    @Override
    protected EntityManagerFactory createContainerEntityManagerFactoryImpl(PersistenceUnitInfo info, Map properties, boolean requiresConnection) {
        // Record that we are inside a JEE container to allow weaving for non managed persistence units.
        JavaSECMPInitializer.setIsInContainer(true);

        Map nonNullProperties = (properties == null) ? new HashMap<>() : properties;

        String forceTargetServer = EntityManagerFactoryProvider.getConfigPropertyAsString(SystemProperties.ENFORCE_TARGET_SERVER, null);
        if ("true".equalsIgnoreCase(forceTargetServer)) {
            nonNullProperties.remove(PersistenceUnitProperties.TARGET_SERVER);
        }

        EntityManagerSetupImpl emSetupImpl = null;
        if (EntityManagerSetupImpl.mustBeCompositeMember(info)) {
            // persistence unit cannot be used standalone (only as a composite member).
            // still the factory will be created but attempt to createEntityManager would cause an exception.
            emSetupImpl = new EntityManagerSetupImpl(info.getPersistenceUnitName(), info.getPersistenceUnitName());
            // predeploy assigns puInfo and does not do anything else.
            // the session is not created, no need to add emSetupImpl to the global map.
            emSetupImpl.predeploy(info, nonNullProperties);
        } else {
            boolean isNew = false;
            ClassTransformer transformer = null;
            String uniqueName = info.getPersistenceUnitName();
            String sessionName = EntityManagerSetupImpl.getOrBuildSessionName(nonNullProperties, info, uniqueName);
            synchronized (EntityManagerFactoryProvider.emSetupImpls) {
                emSetupImpl = EntityManagerFactoryProvider.getEntityManagerSetupImpl(sessionName);
                if (emSetupImpl == null) {
                    emSetupImpl = new EntityManagerSetupImpl(uniqueName, sessionName);
                    isNew = true;
                    emSetupImpl.setIsInContainerMode(true);
                    // if predeploy fails then emSetupImpl shouldn't be added to FactoryProvider
                    transformer = emSetupImpl.predeploy(info, nonNullProperties);
                    EntityManagerFactoryProvider.addEntityManagerSetupImpl(sessionName, emSetupImpl);
                }
            }

            if (!isNew) {
                if (!uniqueName.equals(emSetupImpl.getPersistenceUnitUniqueName())) {
                    throw PersistenceUnitLoadingException.sessionNameAlreadyInUse(sessionName, uniqueName, emSetupImpl.getPersistenceUnitUniqueName());
                }
                // synchronized to prevent undeploying by other threads.
                boolean undeployed = false;
                synchronized (emSetupImpl) {
                    if (emSetupImpl.isUndeployed()) {
                        undeployed = true;
                    } else {
                        // emSetupImpl has been already predeployed, predeploy will just increment factoryCount.
                        transformer = emSetupImpl.predeploy(emSetupImpl.getPersistenceUnitInfo(), nonNullProperties);
                    }
                }
                if (undeployed) {
                    // after the emSetupImpl has been obtained from emSetupImpls
                    // it has been undeployed by factory.close() in another thread - start all over again.
                    return createContainerEntityManagerFactory(info, properties);
                }
            }
            if (transformer != null) {
                info.addTransformer(transformer);
            }
        }

        EntityManagerFactoryImpl factory = null;
        try {
            factory = new EntityManagerFactoryImpl(emSetupImpl, nonNullProperties);
            emSetupImpl.setRequiresConnection(requiresConnection);
            emSetupImpl.preInitializeCanonicalMetamodel(factory);

            // This code has been added to allow validation to occur without actually calling createEntityManager
            if (emSetupImpl.shouldGetSessionOnCreateFactory(nonNullProperties)) {
                factory.getDatabaseSession();
            }
            return factory;
        } catch (RuntimeException ex) {
            if (factory != null) {
                factory.close();
            } else {
                emSetupImpl.undeploy();
            }
            throw ex;
        }
    }
}
