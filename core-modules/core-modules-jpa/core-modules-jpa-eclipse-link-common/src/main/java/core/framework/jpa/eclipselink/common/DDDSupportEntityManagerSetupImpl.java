package core.framework.jpa.eclipselink.common;

import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.TimestampLockingPolicy;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.EntityManagerSetupException;
import org.eclipse.persistence.exceptions.ExceptionHandler;
import org.eclipse.persistence.exceptions.IntegrityException;
import org.eclipse.persistence.exceptions.PersistenceUnitLoadingException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.internal.helper.ConcurrencyUtil;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.helper.JPAClassLoaderHolder;
import org.eclipse.persistence.internal.helper.JPAConversionManager;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.internal.jpa.deployment.BeanValidationInitializationHelper;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProcessor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAsmFactory;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.weaving.ClassDetails;
import org.eclipse.persistence.internal.jpa.weaving.PersistenceWeaver;
import org.eclipse.persistence.internal.jpa.weaving.TransformerFactory;
import org.eclipse.persistence.internal.localization.LoggingLocalization;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.jpa.metadata.MetadataSource;
import org.eclipse.persistence.jpa.metadata.XMLMetadataSource;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.platform.database.converters.StructConverter;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.coordination.MetadataRefreshListener;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;
import org.eclipse.persistence.sessions.server.ServerSession;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static core.framework.jpa.eclipselink.common.EntityManagerFactoryProvider.getConfigProperty;
import static core.framework.jpa.eclipselink.common.EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug;
import static core.framework.jpa.eclipselink.common.EntityManagerFactoryProvider.getConfigPropertyLogDebug;
import static core.framework.jpa.eclipselink.common.EntityManagerFactoryProvider.login;
import static core.framework.jpa.eclipselink.common.EntityManagerFactoryProvider.translateOldProperties;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.NONE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_ACTION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_SCRIPT_SOURCE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_SOURCE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_DROP_ACTION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_DROP_AND_CREATE_ACTION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_DROP_SCRIPT_SOURCE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_DROP_SOURCE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_NONE_ACTION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE;
import static org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider.getConfigPropertyAsString;
import static org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider.hasConfigProperty;
import static org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider.mergeMaps;


/**
 * @author ebin
 */
public class DDDSupportEntityManagerSetupImpl extends EntityManagerSetupImpl implements MetadataRefreshListener {
    private MetadataProcessor processor = null;
    /**
     * Holds a reference to the weaver class transformer so it can be cleared after login.
     */
    private PersistenceWeaver weaver = null;

    /*
     * Composite, not null only if it's a composite member.
     */
    protected DDDSupportEntityManagerSetupImpl compositeEmSetupImpl;

    /*
     * Composite members, not null only if it's a composite.
     */
    protected Set<DDDSupportEntityManagerSetupImpl> compositeMemberEmSetupImpls;

    /*
     * In HalfPredeployedCompositeMember predeploy method called several times,
     * each call uses mode, then updating it before returning.
     * So mode value could be viewed as a substate of HalfPredeployedCompositeMember state.
     * The mode is required for staging of processing OR metadata for composite members:
     * each processing stage should be completed for ALL composite members before
     * any one of then could proceed to the next processing stage.
     */
    PersistenceUnitProcessor.Mode mode;

    boolean throwExceptionOnFail;
    boolean weaveChangeTracking;
    boolean weaveLazy;
    boolean weaveEager;
    boolean weaveFetchGroups;
    boolean weaveInternal;
    boolean weaveRest;
    boolean weaveMappedSuperClass;

    public DDDSupportEntityManagerSetupImpl(String persistenceUnitUniqueName, String sessionName) {
        this.persistenceUnitUniqueName = persistenceUnitUniqueName;
        this.sessionName = sessionName;
        this.requiresConnection = true;
    }

    private static ValidationMode getValidationMode(PersistenceUnitInfo persitenceUnitInfo, Map puProperties) {
        //Check if overridden at emf creation
        String validationModeAtEMFCreation = (String) puProperties.get(PersistenceUnitProperties.VALIDATION_MODE);
        if (validationModeAtEMFCreation != null) {
            // User will receive IllegalArgumentException if an invalid mode has been specified
            return ValidationMode.valueOf(validationModeAtEMFCreation.toUpperCase(Locale.ROOT));
        }

        //otherwise:
        ValidationMode validationMode = null;
        // Initialize with value in persitence.xml
        // Using reflection to call getValidationMode to prevent blowing up while we are running in JPA 1.0 environment
        // (This would be all JavaEE5 appservers) where PersistenceUnitInfo does not implement method getValidationMode().
        try {
            Method method = PrivilegedAccessHelper.getDeclaredMethod(PersistenceUnitInfo.class, "getValidationMode", null);
            validationMode = PrivilegedAccessHelper.invokeMethod(method, persitenceUnitInfo, null);
        } catch (Throwable exception) {
            // We are running in JavaEE5 environment. Catch and swallow any exceptions and return null.
        }

        if (validationMode == null) {
            // Default to AUTO as specified in JPA spec.
            validationMode = ValidationMode.AUTO;
        }
        return validationMode;
    }

    protected void updateMetadataRepository(Map m, ClassLoader loader) {
        Object metadataSource = getConfigPropertyLogDebug(PersistenceUnitProperties.METADATA_SOURCE, m, session);
        if (metadataSource != null && metadataSource instanceof MetadataSource) {
            processor.setMetadataSource((MetadataSource) metadataSource);
        } else {
            if (metadataSource != null) {
                String repository = (String) metadataSource;
                if (repository.equalsIgnoreCase("XML")) {
                    processor.setMetadataSource(new XMLMetadataSource());
                } else {
                    try {
                        Class<? extends MetadataSource> transportClass = findClassForProperty(repository, PersistenceUnitProperties.METADATA_SOURCE, loader);
                        processor.setMetadataSource(transportClass.getConstructor().newInstance());
                    } catch (Exception invalid) {
                        session.handleException(EntityManagerSetupException.failedToInstantiateProperty(repository, PersistenceUnitProperties.METADATA_SOURCE, invalid));
                    }
                }
            }
        }
    }

    protected void updateSharedCacheMode(Map m) {
        String sharedCacheMode = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.SHARED_CACHE_MODE, m, session);
        if (sharedCacheMode != null) {
            processor.getProject().setSharedCacheMode(SharedCacheMode.valueOf(sharedCacheMode));
        }
    }

    public void addStructConverters() {
        if (this.compositeMemberEmSetupImpls == null) {
            for (StructConverter structConverter : structConverters) {
                if (session.getPlatform().getTypeConverters().get(structConverter.getJavaType()) != null) {
                    throw ValidationException.twoStructConvertersAddedForSameClass(structConverter.getJavaType().getName());
                }
                session.getPlatform().addStructConverter(structConverter);
            }
        } else {
            // composite
            for (DDDSupportEntityManagerSetupImpl compositeMemberEmSetupImpl : this.compositeMemberEmSetupImpls) {
                if (!compositeMemberEmSetupImpl.structConverters.isEmpty()) {
                    String compositeMemberPuName = compositeMemberEmSetupImpl.getPersistenceUnitInfo().getPersistenceUnitName();
                    // debug output added to make it easier to navigate the log because the method is called outside of composite member deploy
                    compositeMemberEmSetupImpl.session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "composite_member_begin_call", new Object[]{"addStructConverters", compositeMemberPuName, state});
                    compositeMemberEmSetupImpl.addStructConverters();
                    compositeMemberEmSetupImpl.session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "composite_member_end_call", new Object[]{"addStructConverters", compositeMemberPuName, state});
                }
            }
        }
    }

    protected void updateSession(Map m, ClassLoader loader) {
        if (session == null || (session.isDatabaseSession() && ((DatabaseSessionImpl) session).isLoggedIn())) {
            return;
        }

        // In deploy ServerPlatform could've changed which will affect the loggers.
        boolean serverPlatformChanged = updateServerPlatform(m, loader);
        updateJPQLParser(m);

        if (!session.hasBroker()) {
            updateLoggers(m, serverPlatformChanged, loader);
            updateProfiler(m, loader);
        }

        // log the server platform being used by the session if it has been changed
        if (serverPlatformChanged && session.getSessionLog().shouldLog(SessionLog.FINE)) {
            session.getSessionLog().log(SessionLog.FINE, SessionLog.SERVER,
                "configured_server_platform", session.getServerPlatform().getClass().getName()); // NOI18N
        }

        if (session.isBroker()) {
            PersistenceUnitTransactionType transactionType = persistenceUnitInfo.getTransactionType();
            //bug 5867753: find and override the transaction type using properties
            String transTypeString = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.TRANSACTION_TYPE, m, session);
            if (transTypeString != null) {
                transactionType = PersistenceUnitTransactionType.valueOf(transTypeString);
            }
            ((DatasourceLogin) session.getDatasourceLogin()).setUsesExternalTransactionController(transactionType == PersistenceUnitTransactionType.JTA);
        } else {
            String shouldBindString = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_BIND_PARAMETERS, m, session);
            if (shouldBindString != null) {
                session.getPlatform().setShouldBindAllParameters(Boolean.parseBoolean(shouldBindString));
            }

            String shouldForceBindString = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_FORCE_BIND_PARAMETERS, m, session);
            if (shouldForceBindString != null) {
                session.getPlatform().setShouldForceBindAllParameters(Boolean.parseBoolean(shouldForceBindString));
            }

            String allowPartialBindString = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.JDBC_ALLOW_PARTIAL_PARAMETERS, m, session);
            if (allowPartialBindString != null) {
                session.getPlatform().setShouldBindPartialParameters(Boolean.parseBoolean(allowPartialBindString));
            }

            updateLogins(m);
        }
        if (!session.getDatasourceLogin().shouldUseExternalTransactionController()) {
            session.getServerPlatform().disableJTA();
        }

        if (session.isServerSession()) {
            updatePools((ServerSession) session, m);
            updateConnectionSettings((ServerSession) session, m);
            if (!isSessionLoadedFromSessionsXML) {
                updateDescriptorCacheSettings(m, loader);
            }
            updateConnectionPolicy((ServerSession) session, m);
        }

        if (session.isBroker()) {
            if (this.compositeMemberEmSetupImpls != null) {
                // composite
                Map compositeMemberMapOfProperties = (Map) getConfigProperty(PersistenceUnitProperties.COMPOSITE_UNIT_PROPERTIES, m);
                for (DDDSupportEntityManagerSetupImpl compositeMemberEmSetupImpl : this.compositeMemberEmSetupImpls) {
                    // the properties guaranteed to be non-null after updateCompositeMemberProperties call
                    String compositeMemberPuName = compositeMemberEmSetupImpl.getPersistenceUnitInfo().getPersistenceUnitName();
                    Map compositeMemberProperties = (Map) compositeMemberMapOfProperties.get(compositeMemberPuName);
                    // debug output added to make it easier to navigate the log because the method is called outside of composite member deploy
                    compositeMemberEmSetupImpl.getSession().log(SessionLog.FINEST, SessionLog.PROPERTIES, "composite_member_begin_call", new Object[]{"updateSession", compositeMemberPuName, state});
                    compositeMemberEmSetupImpl.updateSession(compositeMemberProperties, loader);
                    compositeMemberEmSetupImpl.getSession().log(SessionLog.FINEST, SessionLog.PROPERTIES, "composite_member_end_call", new Object[]{"updateSession", compositeMemberPuName, state});
                }
            }
            setSessionEventListener(m, loader);
            setExceptionHandler(m, loader);

            updateAllowZeroIdSetting(m);
            updateCacheCoordination(m, loader);
            processSessionCustomizer(m, loader);
        } else {
            setSessionEventListener(m, loader);
            setExceptionHandler(m, loader);

            updateBatchWritingSetting(m, loader);

            updateNativeSQLSetting(m);
            updateSequencing(m);
            updateSequencingStart(m);
            updateAllowNativeSQLQueriesSetting(m);
            updateSQLCastSetting(m);
            updateUppercaseSetting(m);
            updateCacheStatementSettings(m);
            updateAllowExtendedCacheLogging(m);
            updateAllowExtendedThreadLogging(m);
            updateAllowExtendedThreadLoggingThreadDump(m);
            updateAllowQueryResultsCacheValidation(m);
            updateTemporalMutableSetting(m);
            updateTableCreationSettings(m);
            updateIndexForeignKeys(m);
            if (!session.hasBroker()) {
                updateAllowZeroIdSetting(m);
            }
            updateIdValidation(m);
            updatePessimisticLockTimeout(m);
            updatePessimisticLockTimeoutUnit(m);
            updateQueryTimeout(m);
            updateQueryTimeoutUnit(m);
            updateLockingTimestampDefault(m);
            updateSQLCallDeferralDefault(m);
            updateNamingIntoIndexed(m);
            if (!session.hasBroker()) {
                updateCacheCoordination(m, loader);
            }
            updatePartitioning(m, loader);
            updateDatabaseEventListener(m, loader);
            updateSerializer(m, loader);
            updateShouldOptimizeResultSetAccess(m);
            updateTolerateInvalidJPQL(m);
            updateTenancy(m, loader);
            // ConcurrencyManager properties
            updateConcurrencyManagerWaitTime(m);
            updateConcurrencyManagerBuildObjectCompleteWaitTime(m);
            updateConcurrencyManagerMaxAllowedSleepTime(m);
            updateConcurrencyManagerMaxAllowedFrequencyToProduceTinyDumpLogMessage(m);
            updateConcurrencyManagerMaxAllowedFrequencyToProduceMassiveDumpLogMessage(m);
            updateConcurrencyManagerAllowInterruptedExceptionFired(m);
            updateConcurrencyManagerAllowConcurrencyExceptionToBeFiredUp(m);
            updateConcurrencyManagerAllowTakingStackTraceDuringReadLockAcquisition(m);
            updateConcurrencyManagerUseObjectBuildingSemaphore(m);
            updateConcurrencyManagerUseWriteLockManagerSemaphore(m);
            updateConcurrencyManagerNoOfThreadsAllowedToObjectBuildInParallel(m);
            updateConcurrencyManagerNoOfThreadsAllowedToDoWriteLockManagerAcquireRequiredLocksInParallel(m);
            updateConcurrencySemaphoreMaxTimePermit(m);
            updateConcurrencySemaphoreLogTimeout(m);
            // Customizers should be processed last
            processDescriptorCustomizers(m, loader);
            processSessionCustomizer(m, loader);

            setDescriptorNamedQueries(m);
        }
    }

    protected void deployCompositeMembers(Map deployProperties, ClassLoader realClassLoader) {
        // Don't log these properties - may contain passwords. The properties will be logged by contained persistence units.
        Map compositeMemberMapOfProperties = (Map) getConfigProperty(PersistenceUnitProperties.COMPOSITE_UNIT_PROPERTIES, deployProperties);
        for (EntityManagerSetupImpl compositeMemberEmSetupImpl : this.compositeMemberEmSetupImpls) {
            // the properties guaranteed to be non-null after updateCompositeMemberProperties call
            Map compositeMemberProperties = (Map) compositeMemberMapOfProperties.get(compositeMemberEmSetupImpl.getPersistenceUnitInfo().getPersistenceUnitName());
            compositeMemberEmSetupImpl.deploy(realClassLoader, compositeMemberProperties);
            AbstractSession containedSession = compositeMemberEmSetupImpl.getSession();
            ((SessionBroker) session).registerSession(containedSession.getName(), containedSession);
        }
    }

    protected void updateCompositeMembersProperties(Map compositeProperties) {
        Set<SEPersistenceUnitInfo> compositePuInfos = new HashSet<>(compositeMemberEmSetupImpls.size());
        for (DDDSupportEntityManagerSetupImpl compositeMemberEmSetupImpl : compositeMemberEmSetupImpls) {
            compositePuInfos.add((SEPersistenceUnitInfo) compositeMemberEmSetupImpl.persistenceUnitInfo);
        }
        updateCompositeMembersProperties(compositePuInfos, compositeProperties);
    }

    public void writeDDL(Map props, DatabaseSessionImpl session, ClassLoader classLoader) {
        if (this.compositeMemberEmSetupImpls == null) {
            // Generate the DDL if we find either EclipseLink or JPA DDL generation properties.
            // Note, we do one or the other, that is, we do not mix the properties by default
            // but we may use some with both, e.g. APP_LOCATION. EclipseLink properties
            // override JPA properties.

            if (hasConfigProperty(DDL_GENERATION, props)) {
                // We have EclipseLink DDL generation properties.
                String ddlGeneration = getConfigPropertyAsString(DDL_GENERATION, props).toLowerCase(Locale.ROOT);

                if (!ddlGeneration.equals(NONE)) {
                    writeDDL(ddlGeneration, props, session, classLoader);
                }
            } else {
                // Look for JPA schema generation properties.

                // Schema generation for the database.
                if (hasConfigProperty(SCHEMA_GENERATION_DATABASE_ACTION, props)) {
                    String databaseGenerationAction = getConfigPropertyAsString(SCHEMA_GENERATION_DATABASE_ACTION, props).toLowerCase(Locale.ROOT);

                    if (!databaseGenerationAction.equals(SCHEMA_GENERATION_NONE_ACTION)) {
                        if (databaseGenerationAction.equals(SCHEMA_GENERATION_CREATE_ACTION)) {
                            writeDDL(SCHEMA_GENERATION_CREATE_SOURCE, SCHEMA_GENERATION_CREATE_SCRIPT_SOURCE, TableCreationType.CREATE, props, session, classLoader);
                        } else if (databaseGenerationAction.equals(PersistenceUnitProperties.CREATE_OR_EXTEND)) {
                            writeDDL(SCHEMA_GENERATION_CREATE_SOURCE, SCHEMA_GENERATION_CREATE_SCRIPT_SOURCE, TableCreationType.EXTEND, props, session, classLoader);
                        } else if (databaseGenerationAction.equals(SCHEMA_GENERATION_DROP_ACTION)) {
                            writeDDL(SCHEMA_GENERATION_DROP_SOURCE, SCHEMA_GENERATION_DROP_SCRIPT_SOURCE, TableCreationType.DROP, props, session, classLoader);
                        } else if (databaseGenerationAction.equals(SCHEMA_GENERATION_DROP_AND_CREATE_ACTION)) {
                            writeDDL(SCHEMA_GENERATION_DROP_SOURCE, SCHEMA_GENERATION_DROP_SCRIPT_SOURCE, TableCreationType.DROP, props, session, classLoader);
                            writeDDL(SCHEMA_GENERATION_CREATE_SOURCE, SCHEMA_GENERATION_CREATE_SCRIPT_SOURCE, TableCreationType.CREATE, props, session, classLoader);
                        } else {
                            String validOptions = SCHEMA_GENERATION_CREATE_ACTION + ", " + SCHEMA_GENERATION_DROP_ACTION + ", " + SCHEMA_GENERATION_DROP_AND_CREATE_ACTION + ", " + PersistenceUnitProperties.CREATE_OR_EXTEND;
                            session.log(SessionLog.WARNING, SessionLog.PROPERTIES, "ddl_generation_unknown_property_value", new Object[]{SCHEMA_GENERATION_DATABASE_ACTION, databaseGenerationAction, persistenceUnitInfo.getPersistenceUnitName(), validOptions});
                        }
                    }
                }

                // Schema generation for target scripts.
                if (hasConfigProperty(SCHEMA_GENERATION_SCRIPTS_ACTION, props)) {
                    String scriptsGenerationAction = getConfigPropertyAsString(SCHEMA_GENERATION_SCRIPTS_ACTION, props).toLowerCase(Locale.ROOT);

                    if (!scriptsGenerationAction.equals(SCHEMA_GENERATION_NONE_ACTION)) {
                        if (scriptsGenerationAction.equals(SCHEMA_GENERATION_CREATE_ACTION)) {
                            writeMetadataDDLToScript(TableCreationType.CREATE, props, session, classLoader);
                        } else if (scriptsGenerationAction.equals(PersistenceUnitProperties.CREATE_OR_EXTEND)) {
                            writeMetadataDDLToScript(TableCreationType.EXTEND, props, session, classLoader);
                        } else if (scriptsGenerationAction.equals(SCHEMA_GENERATION_DROP_ACTION)) {
                            writeMetadataDDLToScript(TableCreationType.DROP, props, session, classLoader);
                        } else if (scriptsGenerationAction.equals(SCHEMA_GENERATION_DROP_AND_CREATE_ACTION)) {
                            writeMetadataDDLToScript(TableCreationType.DROP_AND_CREATE, props, session, classLoader);
                        } else {
                            String validOptions = SCHEMA_GENERATION_CREATE_ACTION + ", " + SCHEMA_GENERATION_DROP_ACTION + ", " + SCHEMA_GENERATION_DROP_AND_CREATE_ACTION;
                            session.log(SessionLog.WARNING, SessionLog.PROPERTIES, "ddl_generation_unknown_property_value", new Object[]{SCHEMA_GENERATION_SCRIPTS_ACTION, scriptsGenerationAction, persistenceUnitInfo.getPersistenceUnitName(), validOptions});
                        }
                    }
                }

                // Once we've generated any and all DDL, check for load scripts.
                writeSourceScriptToDatabase(getConfigProperty(SCHEMA_GENERATION_SQL_LOAD_SCRIPT_SOURCE, props), session, classLoader);
            }
        } else {
            // composite
            Map compositeMemberMapOfProperties = (Map) getConfigProperty(PersistenceUnitProperties.COMPOSITE_UNIT_PROPERTIES, props);
            for (EntityManagerSetupImpl compositeMemberEmSetupImpl : this.compositeMemberEmSetupImpls) {
                // the properties guaranteed to be non-null after updateCompositeMemberProperties call
                Map compositeMemberProperties = (Map) compositeMemberMapOfProperties.get(compositeMemberEmSetupImpl.getPersistenceUnitInfo().getPersistenceUnitName());
                compositeMemberEmSetupImpl.writeDDL(compositeMemberProperties, session, classLoader);
            }
        }
    }

    public boolean isComposite() {
        return this.compositeMemberEmSetupImpls != null;
    }

    public boolean isCompositeMember() {
        return this.compositeEmSetupImpl != null;
    }

    public void setCompositeEmSetupImpl(DDDSupportEntityManagerSetupImpl compositeEmSetupImpl) {
        this.compositeEmSetupImpl = compositeEmSetupImpl;
    }

    public DDDSupportEntityManagerSetupImpl getCompositeEmSetupImpl() {
        return this.compositeEmSetupImpl;
    }


    @Override
    public synchronized ClassTransformer predeploy(PersistenceUnitInfo info, Map extendedProperties) {
        ClassLoader classLoaderToUse = null;
        // session == null
        if (state == STATE_DEPLOY_FAILED || state == STATE_UNDEPLOYED) {
            throw new PersistenceException(EntityManagerSetupException.cannotPredeploy(persistenceUnitInfo.getPersistenceUnitName(), state, persistenceException));
        }
        // session != null
        if (state == STATE_PREDEPLOYED || state == STATE_DEPLOYED || state == STATE_HALF_DEPLOYED) {
            session.log(SessionLog.FINEST, SessionLog.JPA, "predeploy_begin", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), session.getName(), state, factoryCount});
            factoryCount++;
            session.log(SessionLog.FINEST, SessionLog.JPA, "predeploy_end", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), session.getName(), state, factoryCount});
            return null;
            // session == null
        } else if (state == STATE_INITIAL) {
            persistenceUnitInfo = info;
            if (!isCompositeMember()) {
                if (mustBeCompositeMember(persistenceUnitInfo)) {
                    if (this.staticWeaveInfo == null) {
                        return null;
                    } else {
                        // predeploy is used for static weaving
                        throw new IllegalStateException(EntityManagerSetupException.compositeMemberCannotBeUsedStandalone(persistenceUnitInfo.getPersistenceUnitName()));
                    }
                }
            }
        }

        // state is INITIAL or PREDEPLOY_FAILED or STATE_HALF_PREDEPLOYED_COMPOSITE_MEMBER, session == null
        try {
            // properties not used in STATE_HALF_PREDEPLOYED_COMPOSITE_MEMBER
            Map predeployProperties = null;
            // composite can't be in STATE_HALF_PREDEPLOYED_COMPOSITE_MEMBER
            boolean isComposite = false;
            if (state != STATE_HALF_PREDEPLOYED_COMPOSITE_MEMBER) {
                //set the claasloader early on and change it if needed
                classLoaderToUse = persistenceUnitInfo.getClassLoader();

                predeployProperties = mergeMaps(extendedProperties, persistenceUnitInfo.getProperties());
                // Translate old properties.
                // This should be done before using properties (i.e. ServerPlatform).
                translateOldProperties(predeployProperties, null);

                String sessionsXMLStr = (String) predeployProperties.get(PersistenceUnitProperties.SESSIONS_XML);
                if (sessionsXMLStr != null) {
                    isSessionLoadedFromSessionsXML = true;
                }

                // Create session (it needs to be done before initializing ServerPlatform and logging).
                // If a sessions-xml is used this will get replaced later, but is required for logging.
                isComposite = isComposite(persistenceUnitInfo);
                if (isComposite) {
                    if (isSessionLoadedFromSessionsXML) {
                        throw EntityManagerSetupException.compositeIncompatibleWithSessionsXml(persistenceUnitInfo.getPersistenceUnitName());
                    }
                    session = new SessionBroker();
                    ((SessionBroker) session).setShouldUseDescriptorAliases(true);
                } else {
                    session = new ServerSession(new Project(new DatabaseLogin()));

                    //set the listener to process RCM metadata refresh commands
                    session.setRefreshMetadataListener(this);
                }
                session.setName(this.sessionName);
                updateTunerPreDeploy(predeployProperties, classLoaderToUse);
                updateTolerateInvalidJPQL(predeployProperties);

                if (this.compositeEmSetupImpl == null) {
                    // session name and ServerPlatform must be set prior to setting the loggers.
                    if (this.staticWeaveInfo == null) {
                        updateServerPlatform(predeployProperties, classLoaderToUse);
                        // Update loggers and settings for the singleton logger and the session logger.
                        updateLoggers(predeployProperties, true, classLoaderToUse);
                        // log the server platform being used by the session
                        if (session.getSessionLog().shouldLog(SessionLog.FINE)) {
                            session.getSessionLog().log(SessionLog.FINE, SessionLog.SERVER,
                                "configured_server_platform", session.getServerPlatform().getClass().getName()); // NOI18N
                        }
                        // Get the temporary classLoader based on the platform

                        //Update performance profiler
                        updateProfiler(predeployProperties, classLoaderToUse);
                    } else {
                        // predeploy is used for static weaving
                        Writer writer = this.staticWeaveInfo.getLogWriter();
                        if (writer != null) {
                            session.getSessionLog().setWriter(writer);
                        }
                        session.setLogLevel(this.staticWeaveInfo.getLogLevel());
                    }
                } else {
                    // composite member
                    session.setSessionLog(this.compositeEmSetupImpl.getSession().getSessionLog());
                    session.setProfiler(this.compositeEmSetupImpl.getSession().getProfiler());
                }

                // Cannot start logging until session and log and initialized, so log start of predeploy here.
                session.log(SessionLog.FINEST, SessionLog.JPA, "predeploy_begin", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), session.getName(), state, factoryCount});

                //Project Cache accessor processing
                updateProjectCache(predeployProperties, classLoaderToUse);

                if (projectCacheAccessor != null) {
                    //get the project from the cache
                    Project project = projectCacheAccessor.retrieveProject(predeployProperties, classLoaderToUse, session.getSessionLog());

                    if (project != null) {
                        try {
                            DatabaseSessionImpl tempSession = (DatabaseSessionImpl) project.createServerSession();

                            tempSession.setName(this.sessionName);
                            tempSession.setSessionLog(session.getSessionLog());
                            tempSession.getSessionLog().setSession(tempSession);
                            if (this.staticWeaveInfo != null) {
                                tempSession.setLogLevel(this.staticWeaveInfo.getLogLevel());
                            }
                            tempSession.setProfiler(session.getProfiler());
                            tempSession.setRefreshMetadataListener(this);

                            session = tempSession;
                            // reusing the serverPlatform from the existing session would have been preferred,
                            //  but its session is only set through the ServerPlatform constructor.
                            updateServerPlatform(predeployProperties, classLoaderToUse);
                            shouldBuildProject = false;
                        } catch (Exception e) {
                            //need a better exception here
                            throw new PersistenceException(e);
                        }
                    }
                }

                if (isSessionLoadedFromSessionsXML) {
                    if (this.compositeEmSetupImpl == null && this.staticWeaveInfo == null) {
                        JPAClassLoaderHolder privateClassLoaderHolder = session.getServerPlatform().getNewTempClassLoader(persistenceUnitInfo);
                        classLoaderToUse = privateClassLoaderHolder.getClassLoader();
                    } else {
                        classLoaderToUse = persistenceUnitInfo.getNewTempClassLoader();
                    }
                    // Loading session from sessions-xml.
                    String tempSessionName = sessionName;
                    if (isCompositeMember()) {
                        // composite member session name is always the same as puName
                        // need the session name specified in properties to read correct session from sessions.xml
                        tempSessionName = (String) predeployProperties.get(PersistenceUnitProperties.SESSION_NAME);
                    }
                    session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "loading_session_xml", sessionsXMLStr, tempSessionName);
                    if (tempSessionName == null) {
                        throw EntityManagerSetupException.sessionNameNeedBeSpecified(persistenceUnitInfo.getPersistenceUnitName(), sessionsXMLStr);
                    }
                    XMLSessionConfigLoader xmlLoader = new XMLSessionConfigLoader(sessionsXMLStr);
                    // Do not register the session with the SessionManager at this point, create temporary session using a local SessionManager and private class loader.
                    // This allows for the project to be accessed without loading any of the classes to allow weaving.
                    // Note that this method assigns sessionName to session.
                    Session tempSession = new SessionManager().getSession(xmlLoader, tempSessionName, classLoaderToUse, false, false);
                    // Load path of sessions-xml resource before throwing error so user knows which sessions-xml file was found (may be multiple).
                    session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "sessions_xml_path_where_session_load_from", xmlLoader.getSessionName(), xmlLoader.getResourcePath());
                    if (tempSession == null) {
                        throw ValidationException.noSessionFound(sessionName, sessionsXMLStr);
                    }
                    // Currently the session must be either a ServerSession or a SessionBroker, cannot be just a DatabaseSessionImpl.
                    if (tempSession.isServerSession() || tempSession.isSessionBroker()) {
                        session = (DatabaseSessionImpl) tempSession;
                        if (tempSessionName != sessionName) {
                            // set back the original session name
                            session.setName(sessionName);
                        }
                    } else {
                        throw EntityManagerSetupException.sessionLoadedFromSessionsXMLMustBeServerSession(persistenceUnitInfo.getPersistenceUnitName(), (String) predeployProperties.get(PersistenceUnitProperties.SESSIONS_XML), tempSession);
                    }
                    if (this.staticWeaveInfo == null) {
                        // Must now reset logging and server-platform on the loaded session.
                        // ServerPlatform must be set prior to setting the loggers.
                        updateServerPlatform(predeployProperties, classLoaderToUse);
                        // Update loggers and settings for the singleton logger and the session logger.
                        updateLoggers(predeployProperties, true, classLoaderToUse);
                    }
                } else {
                    classLoaderToUse = persistenceUnitInfo.getClassLoader();
                }

                EntityManagerFactoryProvider.warnOldProperties(predeployProperties, session);
                session.getPlatform().setConversionManager(new JPAConversionManager());

                if (this.staticWeaveInfo == null) {
                    if (!isComposite) {
                        PersistenceUnitTransactionType transactionType = null;
                        //bug 5867753: find and override the transaction type
                        String transTypeString = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.TRANSACTION_TYPE, predeployProperties, session);
                        if (transTypeString != null && transTypeString.length() > 0) {
                            transactionType = PersistenceUnitTransactionType.valueOf(transTypeString);
                        } else if (persistenceUnitInfo != null) {
                            transactionType = persistenceUnitInfo.getTransactionType();
                        }

                        if (!isValidationOnly(predeployProperties, false) && persistenceUnitInfo != null && transactionType == PersistenceUnitTransactionType.JTA) {
                            if (predeployProperties.get(PersistenceUnitProperties.JTA_DATASOURCE) == null && persistenceUnitInfo.getJtaDataSource() == null) {
                                if (predeployProperties.get(PersistenceUnitProperties.SCHEMA_DATABASE_PRODUCT_NAME) == null ||
                                    predeployProperties.get(PersistenceUnitProperties.SCHEMA_DATABASE_MAJOR_VERSION) == null ||
                                    predeployProperties.get(PersistenceUnitProperties.SCHEMA_DATABASE_MINOR_VERSION) == null) {
                                    throw EntityManagerSetupException.jtaPersistenceUnitInfoMissingJtaDataSource(persistenceUnitInfo.getPersistenceUnitName());
                                }
                            }
                        }
                    }

                    // this flag is used to disable work done as a result of the LAZY hint on OneToOne and ManyToOne mappings
                    if (state == STATE_INITIAL) {
                        if (compositeEmSetupImpl == null) {
                            if (null == enableWeaving) {
                                enableWeaving = Boolean.TRUE;
                            }
                            isWeavingStatic = false;
                            String weaving = getConfigPropertyAsString(PersistenceUnitProperties.WEAVING, predeployProperties);

                            if (weaving != null && weaving.equalsIgnoreCase("false")) {
                                enableWeaving = Boolean.FALSE;
                            } else if (weaving != null && weaving.equalsIgnoreCase("static")) {
                                isWeavingStatic = true;
                            }
                        } else {
                            // composite member
                            // no weaving for composite forces no weaving for members
                            if (!compositeEmSetupImpl.enableWeaving) {
                                enableWeaving = Boolean.FALSE;
                            } else {
                                if (null == enableWeaving) {
                                    enableWeaving = Boolean.TRUE;
                                }
                                String weaving = getConfigPropertyAsString(PersistenceUnitProperties.WEAVING, predeployProperties);
                                if (weaving != null && weaving.equalsIgnoreCase("false")) {
                                    enableWeaving = Boolean.FALSE;
                                }
                            }
                            // static weaving is dictated by composite
                            isWeavingStatic = compositeEmSetupImpl.isWeavingStatic;
                        }
                    }

                    if (compositeEmSetupImpl == null) {
                        throwExceptionOnFail = "true".equalsIgnoreCase(
                            getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.THROW_EXCEPTIONS, predeployProperties, "true", session));
                    } else {
                        // composite member
                        throwExceptionOnFail = compositeEmSetupImpl.throwExceptionOnFail;
                    }
                } else {
                    // predeploy is used for static weaving
                    enableWeaving = Boolean.TRUE;
                }

                weaveChangeTracking = false;
                weaveLazy = false;
                weaveEager = false;
                weaveFetchGroups = false;
                weaveInternal = false;
                weaveRest = false;
                weaveMappedSuperClass = false;
                if (enableWeaving) {
                    weaveChangeTracking = "true".equalsIgnoreCase(getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.WEAVING_CHANGE_TRACKING, predeployProperties, "true", session));
                    weaveLazy = "true".equalsIgnoreCase(getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.WEAVING_LAZY, predeployProperties, "true", session));
                    weaveEager = "true".equalsIgnoreCase(getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.WEAVING_EAGER, predeployProperties, "false", session));
                    weaveFetchGroups = "true".equalsIgnoreCase(getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.WEAVING_FETCHGROUPS, predeployProperties, "true", session));
                    weaveInternal = "true".equalsIgnoreCase(getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.WEAVING_INTERNAL, predeployProperties, "true", session));
                    weaveRest = "true".equalsIgnoreCase(getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.WEAVING_REST, predeployProperties, shouldWeaveRestByDefault(classLoaderToUse), session));
                    weaveMappedSuperClass = "true".equalsIgnoreCase(getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.WEAVING_MAPPEDSUPERCLASS, predeployProperties, "true", session));
                }

            }
            if (shouldBuildProject && !isSessionLoadedFromSessionsXML) {
                if (isComposite) {
                    predeployCompositeMembers(predeployProperties, classLoaderToUse);
                } else {
                    MetadataProcessor compositeProcessor = null;
                    if (compositeEmSetupImpl == null) {
                        mode = PersistenceUnitProcessor.Mode.ALL;
                    } else {
                        // composite member
                        if (state != STATE_HALF_PREDEPLOYED_COMPOSITE_MEMBER) {
                            state = STATE_HALF_PREDEPLOYED_COMPOSITE_MEMBER;
                            mode = PersistenceUnitProcessor.Mode.COMPOSITE_MEMBER_INITIAL;
                        }
                        compositeProcessor = compositeEmSetupImpl.processor;
                    }

                    if (mode == PersistenceUnitProcessor.Mode.ALL || mode == PersistenceUnitProcessor.Mode.COMPOSITE_MEMBER_INITIAL) {
                        boolean usesMultitenantSharedEmf = "true".equalsIgnoreCase(getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.MULTITENANT_SHARED_EMF, predeployProperties, "true", session));
                        boolean usesMultitenantSharedCache = "true".equalsIgnoreCase(getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.MULTITENANT_SHARED_CACHE, predeployProperties, "false", session));

                        // Create an instance of MetadataProcessor for specified persistence unit info
                        processor = new DDDMetadataProcessor(persistenceUnitInfo, session, classLoaderToUse, weaveLazy, weaveEager, weaveFetchGroups, usesMultitenantSharedEmf, usesMultitenantSharedCache, predeployProperties, compositeProcessor);

                        //need to use the real classloader to create the repository class
                        updateMetadataRepository(predeployProperties, classLoaderToUse);

                        //bug:299926 - Case insensitive table / column matching with native SQL queries
                        EntityManagerSetupImpl.updateCaseSensitivitySettings(predeployProperties, processor.getProject(), session);
                    }

                    // Set the shared cache mode to the jakarta.persistence.sharedCache.mode property value.
                    updateSharedCacheMode(predeployProperties);

                    // Process the Object/relational metadata from XML and annotations.
                    // If Java Security is enabled, surround this call with a doPrivileged block.
                    PersistenceUnitProcessor.processORMetadata(processor, throwExceptionOnFail, mode);

                    if (mode == PersistenceUnitProcessor.Mode.COMPOSITE_MEMBER_INITIAL) {
                        mode = PersistenceUnitProcessor.Mode.COMPOSITE_MEMBER_MIDDLE;
                        session.log(SessionLog.FINEST, SessionLog.JPA, "predeploy_end", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), session.getName(), state + " " + mode, factoryCount});
                        return null;
                    } else if (mode == PersistenceUnitProcessor.Mode.COMPOSITE_MEMBER_MIDDLE) {
                        mode = PersistenceUnitProcessor.Mode.COMPOSITE_MEMBER_FINAL;
                        session.log(SessionLog.FINEST, SessionLog.JPA, "predeploy_end", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), session.getName(), state + " " + mode, factoryCount});
                        return null;
                    }
                    // mode == PersistenceUnitProcessor.Mode.ALL || mode == PersistenceUnitProcessor.Mode.COMPOSITE_MEMBER_FINAL
                    // clear mode and proceed
                    mode = null;

                    if (session.getIntegrityChecker().hasErrors()) {
                        session.handleException(new IntegrityException(session.getIntegrityChecker()));
                    }

                    // The transformer is capable of altering domain classes to handle a LAZY hint for OneToOne mappings.  It will only
                    // be returned if we we are mean to process these mappings
                    if (enableWeaving) {
                        // build a list of entities the persistence unit represented by this EntityManagerSetupImpl will use
                        Collection<MetadataClass> entities = PersistenceUnitProcessor.buildEntityList(processor, classLoaderToUse);
                        this.weaver = TransformerFactory.createTransformerAndModifyProject(session, entities, classLoaderToUse, weaveLazy, weaveChangeTracking, weaveFetchGroups, weaveInternal, weaveRest, weaveMappedSuperClass);
                        session.getProject().setClassNamesForWeaving(new ArrayList<>(processor.getProject().getWeavableClassNames()));
                    }

                    //moved from deployment:
                    processor.addNamedQueries();
                    processor.addStructConverterNames();
                }
            } else {
                //This means this session is from sessions.xml or a cached project

                // The transformer is capable of altering domain classes to handle a LAZY hint for OneToOne mappings.  It will only
                // be returned if we we are meant to process these mappings.
                if (enableWeaving) {
                    Collection<MetadataClass> persistenceClasses = new ArrayList<>();
                    MetadataAsmFactory factory = new MetadataAsmFactory(new MetadataLogger(session), classLoaderToUse);
                    if (shouldBuildProject) {
                        // If deploying from a sessions-xml it is still desirable to allow the classes to be weaved.
                        // build a list of entities the persistence unit represented by this EntityManagerSetupImpl will use
                        for (Iterator<Class<?>> iterator = session.getProject().getDescriptors().keySet().iterator(); iterator.hasNext(); ) {
                            persistenceClasses.add(factory.getMetadataClass(iterator.next().getName()));
                        }
                    } else {
                        // build a list of entities the persistence unit represented by this EntityManagerSetupImpl will use
                        for (String className : session.getProject().getClassNamesForWeaving()) {
                            persistenceClasses.add(factory.getMetadataClass(className));
                        }
                    }
                    this.weaver = TransformerFactory.createTransformerAndModifyProject(session, persistenceClasses, classLoaderToUse, weaveLazy, weaveChangeTracking, weaveFetchGroups, weaveInternal, weaveRest, weaveMappedSuperClass);
                }
            }

            // composite member never has a factory - it is predeployed by the composite.
            if (!isCompositeMember()) {
                // factoryCount is not incremented only in case of a first call to preDeploy
                // in non-container mode: this call is not associated with a factory
                // but rather done by JavaSECMPInitializer.callPredeploy (typically in preMain).
                if (state != STATE_INITIAL || this.isInContainerMode()) {
                    factoryCount++;
                }
                preInitializeMetamodel();
            }

            state = STATE_PREDEPLOYED;
            session.log(SessionLog.FINEST, SessionLog.JPA, "predeploy_end", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), session.getName(), state, factoryCount});
            //gf3146: if static weaving is used, we should not return a transformer.  Transformer should still be created though as it modifies descriptors
            if (isWeavingStatic) {
                return null;
            } else {
                return this.weaver;
            }
        } catch (Throwable ex) {
            state = STATE_PREDEPLOY_FAILED;
            // cache this.persistenceException before slow logging
            PersistenceException persistenceEx = createPredeployFailedPersistenceException(ex);
            // If session exists, use it for logging
            if (session != null) {
                session.log(SessionLog.FINEST, SessionLog.JPA, "predeploy_end", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), session.getName(), state, factoryCount});
                // If at least staticWeaveInfo exists, use it for logging
            } else if (staticWeaveInfo != null && staticWeaveInfo.getLogLevel() <= SessionLog.FINEST) {
                Writer logWriter = staticWeaveInfo.getLogWriter();
                if (logWriter != null) {
                    String message = LoggingLocalization.buildMessage("predeploy_end", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), "N/A", state, factoryCount});
                    try {
                        logWriter.write(message);
                        logWriter.write(Helper.cr());
                    } catch (IOException ioex) {
                        // Ignore IOException
                    }
                }
            }
            session = null;
            mode = null;
            throw persistenceEx;
        }
    }

    @Override
    public AbstractSession deploy(ClassLoader realClassLoader, Map additionalProperties) {
        if (this.state != STATE_PREDEPLOYED && this.state != STATE_DEPLOYED && this.state != STATE_HALF_DEPLOYED) {
            if (mustBeCompositeMember()) {
                throw new IllegalStateException(EntityManagerSetupException.compositeMemberCannotBeUsedStandalone(this.persistenceUnitInfo.getPersistenceUnitName()));
            }
            throw new PersistenceException(EntityManagerSetupException.cannotDeployWithoutPredeploy(this.persistenceUnitInfo.getPersistenceUnitName(), this.state, this.persistenceException));
        }
        // state is PREDEPLOYED or DEPLOYED
        this.session.log(SessionLog.FINEST, SessionLog.JPA, "deploy_begin", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), this.session.getName(), this.state, this.factoryCount});

        ClassLoader classLoaderToUse = realClassLoader;

        if (additionalProperties.containsKey(PersistenceUnitProperties.CLASSLOADER)) {
            classLoaderToUse = (ClassLoader) additionalProperties.get(PersistenceUnitProperties.CLASSLOADER);
        } else if ((this.processor != null) && (this.processor.getProject() != null) && (this.processor.getProject().hasVirtualClasses()) && (this.state == STATE_PREDEPLOYED) && (!(classLoaderToUse instanceof DynamicClassLoader))) {
            classLoaderToUse = new DynamicClassLoader(classLoaderToUse);
        }

        // indicates whether session has failed to connect, determines whether HALF_DEPLOYED state should be kept in case of exception.
        boolean isLockAcquired = false;
        try {
            Map deployProperties = mergeMaps(additionalProperties, this.persistenceUnitInfo.getProperties());
            updateTunerPreDeploy(deployProperties, classLoaderToUse);
            translateOldProperties(deployProperties, this.session);
            if (isComposite()) {
                updateCompositeMembersProperties(deployProperties);
            }
            if (this.state == STATE_PREDEPLOYED) {
                this.deployLock.acquire();
                isLockAcquired = true;
                if (this.state == STATE_PREDEPLOYED) {
                    if (this.shouldBuildProject && !this.isSessionLoadedFromSessionsXML) {
                        if (isComposite()) {
                            deployCompositeMembers(deployProperties, classLoaderToUse);
                        } else {
                            if (this.processor.getMetadataSource() != null) {
                                Map metadataProperties = this.processor.getMetadataSource().getPropertyOverrides(deployProperties, classLoaderToUse, this.session.getSessionLog());
                                if (metadataProperties != null && !metadataProperties.isEmpty()) {
                                    translateOldProperties(metadataProperties, this.session);
                                    deployProperties = mergeMaps(metadataProperties, deployProperties);
                                }
                            }
                            // listeners and queries require the real classes and are therefore built during deploy using the realClassLoader
                            this.processor.setClassLoader(classLoaderToUse);
                            this.processor.createDynamicClasses();

                            this.processor.addEntityListeners();

                            if (this.projectCacheAccessor != null) {
                                //cache the project:
                                this.projectCacheAccessor.storeProject(this.session.getProject(), deployProperties, this.session.getSessionLog());
                            }

                            // The project is initially created using class names rather than classes.  This call will make the conversion.
                            // If the session was loaded from sessions.xml this will also convert the descriptor classes to the correct class loader.
                            this.session.getProject().convertClassNamesToClasses(classLoaderToUse);

                            if (!isCompositeMember()) {
                                addBeanValidationListeners(deployProperties, classLoaderToUse);
                            }

                            // Process the customizers last.
                            this.processor.processCustomizers();
                        }

                        this.processor = null;
                    } else {
                        // The project is initially created using class names rather than classes.  This call will make the conversion.
                        // If the session was loaded from sessions.xml this will also convert the descriptor classes to the correct class loader.
                        this.session.getProject().convertClassNamesToClasses(classLoaderToUse);
                        if (!this.shouldBuildProject) {
                            //process anything that might not have been serialized/cached in the project correctly:
                            if (!isCompositeMember()) {
                                addBeanValidationListeners(deployProperties, classLoaderToUse);
                            }

                            //process Descriptor customizers:
                            processDescriptorsFromCachedProject(classLoaderToUse);
                        }
                    }
                    finishProcessingDescriptorEvents(classLoaderToUse);
                    this.structConverters = getStructConverters(classLoaderToUse);

                    updateRemote(deployProperties, classLoaderToUse);
                    initSession();

                    if (this.session.getIntegrityChecker().hasErrors()) {
                        this.session.handleException(new IntegrityException(session.getIntegrityChecker()));
                    }

                    this.session.getDatasourcePlatform().getConversionManager().setLoader(classLoaderToUse);
                    this.state = STATE_HALF_DEPLOYED;
                    // keep deployLock
                } else {
                    // state is HALF_DEPLOYED or DEPLOY_FAILED
                    this.deployLock.release();
                    isLockAcquired = false;
                    if (this.state == STATE_DEPLOY_FAILED) {
                        // while this thread waited in STATE_PREDEPLOYED another thread attempted to deploy and failed.
                        // Rethrow the cache PersistenceException, which caused STATE_DEPLOYED_FAILED.
                        throw persistenceException;
                    }
                }
            }
            // state is HALF_DEPLOYED or DEPLOYED
            if (!isCompositeMember()) {
                if (this.session.isDatabaseSession() && !((DatabaseSessionImpl) session).isLoggedIn()) {
                    // If it's HALF_DEPLOYED then deployLock has been already acquired.
                    if (!isLockAcquired) {
                        this.deployLock.acquire();
                        isLockAcquired = true;
                    }
                    if (!((DatabaseSessionImpl) this.session).isLoggedIn()) {
                        if (this.state == STATE_DEPLOY_FAILED) {
                            // while this thread waited in STATE_HALF_DEPLOYED another thread attempted to connect the session and failed.
                            // Rethrow the cache PersistenceException, which caused STATE_DEPLOYED_FAILED.
                            throw persistenceException;
                        }
                        this.session.setProperties(deployProperties);
                        updateSession(deployProperties, classLoaderToUse);
                        if (isValidationOnly(deployProperties, false)) {
                            /**
                             * for 324213 we could add a session.loginAndDetectDatasource() call
                             * before calling initializeDescriptors when validation-only is True
                             * to avoid a native sequence exception on a generic DatabasePlatform
                             * by auto-detecting the correct DB platform.
                             * However, this would introduce a DB login when validation is on
                             * - in opposition to the functionality of the property (to only validate)
                             */
                            if (this.state == STATE_HALF_DEPLOYED) {
                                getDatabaseSession().initializeDescriptors();
                                this.state = STATE_DEPLOYED;
                            }
                        } else {
                            try {
                                updateTunerDeploy(deployProperties, classLoaderToUse);
                                updateFreeMemory(deployProperties);
                                if (this.isSessionLoadedFromSessionsXML) {
                                    getDatabaseSession().login();
                                } else {
                                    login(getDatabaseSession(), deployProperties, requiresConnection);
                                }

                                // Make JTA integration throw JPA exceptions.
                                if (this.session.hasExternalTransactionController()) {
                                    if (this.session.getExternalTransactionController().getExceptionHandler() == null) {
                                        this.session.getExternalTransactionController().setExceptionHandler(new ExceptionHandler() {

                                            @Override
                                            public Object handleException(RuntimeException exception) {
                                                if (exception instanceof org.eclipse.persistence.exceptions.OptimisticLockException) {
                                                    throw new OptimisticLockException(exception);
                                                } else if (exception instanceof EclipseLinkException) {
                                                    throw new PersistenceException(exception);
                                                } else {
                                                    throw exception;
                                                }
                                            }

                                        });
                                    }
                                }
                                this.state = STATE_DEPLOYED;
                            } catch (Throwable loginException) {
                                if (this.state == STATE_HALF_DEPLOYED) {
                                    if (this.session.isConnected()) {
                                        // session is connected, but postConnect has failed.
                                        // Likely this is caused by failure in initializeDescriptors:
                                        // either descriptor exception or by invalid named jpql query.
                                        // Cannot recover from that - the user has to fix the persistence unit and redeploy it.
                                        try {
                                            getDatabaseSession().logout();
                                        } catch (Throwable logoutException) {
                                            // Ignore
                                        }
                                        this.state = STATE_DEPLOY_FAILED;
                                    }
                                }
                                throw loginException;
                            }
                            if (!this.isSessionLoadedFromSessionsXML) {
                                addStructConverters();
                            }

                            // Generate the DDL using the correct connection.
                            writeDDL(deployProperties, getDatabaseSession(deployProperties), classLoaderToUse);
                        }
                    }
                    // Initialize platform specific identity sequences.
                    session.getDatasourcePlatform().initIdentitySequences(getDatabaseSession(), MetadataProject.DEFAULT_IDENTITY_GENERATOR);
                    updateTunerPostDeploy(deployProperties, classLoaderToUse);
                    this.deployLock.release();
                    isLockAcquired = false;
                }
                // 266912: Initialize the Metamodel, a login should have already occurred.
                try {
                    this.getMetamodel(classLoaderToUse);
                } catch (Exception e) {
                    this.session.log(SessionLog.FINEST, SessionLog.METAMODEL, "metamodel_init_failed", new Object[]{e.getMessage()});
                }
            }
            // Clear the weaver's reference to meta-data information, as it is held by the class loader and will never gc.
            if (this.weaver != null) {
                this.weaver.clear();
                this.weaver = null;
            }

            return this.session;
        } catch (Throwable exception) {
            // before releasing deployLock switch to the correct state
            if (this.state == STATE_PREDEPLOYED) {
                this.state = STATE_DEPLOY_FAILED;
            }
            PersistenceException persistenceEx;
            if (this.state == STATE_DEPLOY_FAILED) {
                if (exception == persistenceException) {
                    persistenceEx = new PersistenceException(EntityManagerSetupException.cannotDeployWithoutPredeploy(this.persistenceUnitInfo.getPersistenceUnitName(), this.state, this.persistenceException));
                } else {
                    // before releasing deployLock cache the exception
                    persistenceEx = createDeployFailedPersistenceException(exception);
                }
            } else {
                if (exception instanceof PersistenceException) {
                    persistenceEx = (PersistenceException) exception;
                } else {
                    persistenceEx = new PersistenceException(exception);
                }
            }
            if (isLockAcquired) {
                this.deployLock.release();
            }
            this.session.logThrowable(SessionLog.SEVERE, SessionLog.EJB, exception);
            throw persistenceEx;
        } finally {
            this.session.log(SessionLog.FINEST, SessionLog.JPA, "deploy_end", new Object[]{getPersistenceUnitInfo().getPersistenceUnitName(), this.session.getName(), this.state, this.factoryCount});
        }
    }

    protected void predeployCompositeMembers(Map predeployProperties, ClassLoader tempClassLoader) {
        // get all puInfos found in jar-files specified in composite's persistence.xml
        // all these puInfos are not composite because composites are recursively "taken apart", too.
        Set<SEPersistenceUnitInfo> compositeMemberPuInfos = getCompositeMemberPuInfoSet(persistenceUnitInfo, predeployProperties);
        // makes sure each member has a non-null property, overrides where required properties with composite's predeploy properties.
        updateCompositeMembersProperties(compositeMemberPuInfos, predeployProperties);
        // Don't log these properties - may contain passwords. The properties will be logged by contained persistence units.
        Map compositeMemberMapOfProperties = (Map) getConfigProperty(PersistenceUnitProperties.COMPOSITE_UNIT_PROPERTIES, predeployProperties);
        this.compositeMemberEmSetupImpls = new HashSet<>(compositeMemberPuInfos.size());
        this.processor = new DDDMetadataProcessor();
        if (enableWeaving) {
            this.weaver = new PersistenceWeaver(new HashMap<String, ClassDetails>());
        }

        // create containedEmSetupImpls and predeploy them for the first time.
        // predeploy divided in three stages (modes):
        // all composite members should complete a stage before any of them can move to the next one.
        for (SEPersistenceUnitInfo compositeMemberPuInfo : compositeMemberPuInfos) {
            // set composite's temporary classloader
            compositeMemberPuInfo.setNewTempClassLoader(tempClassLoader);
            String containedPuName = compositeMemberPuInfo.getPersistenceUnitName();
            DDDSupportEntityManagerSetupImpl containedEmSetupImpl = new DDDSupportEntityManagerSetupImpl(containedPuName, containedPuName);
            // set composite
            containedEmSetupImpl.setCompositeEmSetupImpl(this);
            // non-null only in case predeploy is used for static weaving
            containedEmSetupImpl.setStaticWeaveInfo(this.staticWeaveInfo);
            // the properties guaranteed to be non-null after updateCompositeMemberProperties call
            Map compositeMemberProperties = (Map) compositeMemberMapOfProperties.get(containedPuName);
            containedEmSetupImpl.predeploy(compositeMemberPuInfo, compositeMemberProperties);
            // reset temporary classloader back to the original
            compositeMemberPuInfo.setNewTempClassLoader(compositeMemberPuInfo.getClassLoader());
            this.compositeMemberEmSetupImpls.add(containedEmSetupImpl);
        }

        // after the first loop containedEmSetupImpls are in HalfPredeployed state,
        // mode = COMPOSITE_MEMBER_MIDDLE mode
        for (EntityManagerSetupImpl containedEmSetupImpl : this.compositeMemberEmSetupImpls) {
            // properties not used, puInfo already set
            containedEmSetupImpl.predeploy(null, null);
        }

        // after the second loop containedEmSetupImpls are still in HalfPredeployed state,
        // mode = COMPOSITE_MEMBER_FINAL mode
        for (EntityManagerSetupImpl containedEmSetupImpl : this.compositeMemberEmSetupImpls) {
            // properties not used, puInfo already set
            PersistenceWeaver containedWeaver = (PersistenceWeaver) containedEmSetupImpl.predeploy(null, null);
            // containedEmSetupImpl is finally in Predeployed state.
            // if both composite and composite member weavings enabled copy class details from member's weaver to composite's one.
            if (enableWeaving && containedWeaver != null) {
                this.weaver.getClassDetailsMap().putAll(containedWeaver.getClassDetailsMap());
            }
        }

        if (enableWeaving && this.weaver.getClassDetailsMap().isEmpty()) {
            this.weaver = null;
        }
    }


    private void addBeanValidationListeners(Map puProperties, ClassLoader appClassLoader) {
        ValidationMode validationMode = getValidationMode(persistenceUnitInfo, puProperties);
        if (validationMode == ValidationMode.AUTO || validationMode == ValidationMode.CALLBACK) {
            // BeanValidationInitializationHelper contains static reference to jakarta.validation.* classes. We need to support
            // environment where these classes are not available.
            // To guard against some vms that eagerly resolve, reflectively load class to prevent any static reference to it
            String helperClassName = "org.eclipse.persistence.internal.jpa.deployment.BeanValidationInitializationHelper$BeanValidationInitializationHelperImpl";
            Class<?> helperClass;
            try {
                try {
                    helperClass = PrivilegedAccessHelper.getClassForName(helperClassName, true, appClassLoader);
                } catch (Throwable t) {
                    // Try the ClassLoader that loaded Eclipselink classes
                    ClassLoader eclipseLinkClassLoader = EntityManagerSetupImpl.class.getClassLoader();
                    helperClass = PrivilegedAccessHelper.getClassForName(helperClassName, true, eclipseLinkClassLoader);
                }
                BeanValidationInitializationHelper beanValidationInitializationHelper = (BeanValidationInitializationHelper) helperClass.getConstructor().newInstance();
                beanValidationInitializationHelper.bootstrapBeanValidation(puProperties, session, appClassLoader);
            } catch (Throwable e) {  //Catching Throwable to catch any linkage errors on vms that resolve eagerly
                if (validationMode == ValidationMode.CALLBACK) {
                    throw PersistenceUnitLoadingException.exceptionObtainingRequiredBeanValidatorFactory(e);
                } // else validationMode == ValidationMode.AUTO. Log a message, Ignore the exception
                this.session.log(SessionLog.FINEST, SessionLog.JPA, "validation_factory_not_initialized", new Object[]{e.getMessage()});
            }
        }
    }


    private void finishProcessingDescriptorEvents(ClassLoader realClassLoader) {
        for (ClassDescriptor descriptor : session.getProject().getDescriptors().values()) {
            if (descriptor.hasEventManager()) {
                descriptor.getEventManager().processDescriptorEventHolders(session, realClassLoader);
            }
        }
    }

    private void processDescriptorsFromCachedProject(ClassLoader realClassLoader) throws ClassNotFoundException, PrivilegedActionException, IllegalAccessException, InstantiationException {
        for (ClassDescriptor descriptor : session.getProject().getDescriptors().values()) {
            //process customizers:
            if (descriptor.getDescriptorCustomizerClassName() != null) {
                Class<?> listenerClass = findClass(descriptor.getDescriptorCustomizerClassName(), realClassLoader);
                DescriptorCustomizer customizer = (DescriptorCustomizer) buildObjectForClass(listenerClass, DescriptorCustomizer.class);
                try {
                    customizer.customize(descriptor);
                } catch (Exception e) {
                    session.getSessionLog().logThrowable(SessionLog.FINER, SessionLog.METADATA, e);
                }
            }
        }
    }

    private String shouldWeaveRestByDefault(ClassLoader cl) {
        try {
            cl.loadClass("org.eclipse.persistence.internal.jpa.rs.weaving.PersistenceWeavedRest");
            return "true";
        } catch (Throwable t) {
            //ignore
        }
        return "false";
    }

    private void updateQueryTimeout(Map persistenceProperties) {
        String timeout = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.QUERY_TIMEOUT, persistenceProperties, session);
        try {
            if (timeout != null) {
                session.setQueryTimeoutDefault(Integer.parseInt(timeout));
            }
        } catch (NumberFormatException exception) {
            this.session.handleException(ValidationException.invalidValueForProperty(timeout, PersistenceUnitProperties.QUERY_TIMEOUT, exception));
        }
    }

    //Bug #456067: Added persistence unit support for timeout units
    private void updateQueryTimeoutUnit(Map persistenceProperties) {
        String timeoutUnit = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.QUERY_TIMEOUT_UNIT, persistenceProperties, session);
        try {
            if (timeoutUnit != null) {
                TimeUnit unit = TimeUnit.valueOf(timeoutUnit);
                session.setQueryTimeoutUnitDefault(unit);
            }
        } catch (IllegalArgumentException exception) {
            this.session.handleException(ValidationException.invalidValueForProperty(timeoutUnit, PersistenceUnitProperties.QUERY_TIMEOUT_UNIT, exception));
        }
    }

    private void updateLockingTimestampDefault(Map persistenceProperties) {
        String local = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.USE_LOCAL_TIMESTAMP, persistenceProperties, session);
        try {
            if (local != null) {
                for (ClassDescriptor descriptor : session.getProject().getDescriptors().values()) {
                    OptimisticLockingPolicy policy = descriptor.getOptimisticLockingPolicy();
                    if (policy instanceof TimestampLockingPolicy) {
                        ((TimestampLockingPolicy) policy).setUsesServerTime(!Boolean.parseBoolean(local));
                    }
                }
            }
        } catch (NumberFormatException exception) {
            this.session.handleException(ValidationException.invalidValueForProperty(local, PersistenceUnitProperties.USE_LOCAL_TIMESTAMP, exception));
        }
    }

    private void updateSQLCallDeferralDefault(Map persistenceProperties) {
        String defer = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.SQL_CALL_DEFERRAL, persistenceProperties, this.session);
        if (defer != null) {
            if (defer.equalsIgnoreCase("true")) {
                this.session.getProject().setAllowSQLDeferral(true);
            } else if (defer.equalsIgnoreCase("false")) {
                this.session.getProject().setAllowSQLDeferral(false);
            } else {
                this.session.handleException(ValidationException.invalidBooleanValueForProperty(defer, PersistenceUnitProperties.SQL_CALL_DEFERRAL));
            }
        }
    }

    private void updateNamingIntoIndexed(Map persistenceProperties) {
        String namingIntoIndexed = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.NAMING_INTO_INDEXED, persistenceProperties, this.session);
        if (namingIntoIndexed != null) {
            if (namingIntoIndexed.equalsIgnoreCase("true")) {
                this.session.getProject().setNamingIntoIndexed(true);
            } else if (namingIntoIndexed.equalsIgnoreCase("false")) {
                this.session.getProject().setNamingIntoIndexed(false);
            } else {
                this.session.handleException(ValidationException.invalidBooleanValueForProperty(namingIntoIndexed, PersistenceUnitProperties.NAMING_INTO_INDEXED));
            }
        }
    }

    private void updateConcurrencyManagerWaitTime(Map persistenceProperties) {
        String acquireWaitTime = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.CONCURRENCY_MANAGER_ACQUIRE_WAIT_TIME, persistenceProperties, session);
        try {
            if (acquireWaitTime != null) {
                ConcurrencyUtil.SINGLETON.setAcquireWaitTime(Long.parseLong(acquireWaitTime));
            }
        } catch (NumberFormatException exception) {
            this.session.handleException(ValidationException.invalidValueForProperty(acquireWaitTime, PersistenceUnitProperties.CONCURRENCY_MANAGER_ACQUIRE_WAIT_TIME, exception));
        }
    }

    private void updateConcurrencyManagerBuildObjectCompleteWaitTime(Map persistenceProperties) {
        String buildObjectCompleteWaitTime = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.CONCURRENCY_MANAGER_BUILD_OBJECT_COMPLETE_WAIT_TIME, persistenceProperties, session);
        try {
            if (buildObjectCompleteWaitTime != null) {
                ConcurrencyUtil.SINGLETON.setBuildObjectCompleteWaitTime(Long.parseLong(buildObjectCompleteWaitTime));
            }
        } catch (NumberFormatException exception) {
            this.session.handleException(ValidationException.invalidValueForProperty(buildObjectCompleteWaitTime, PersistenceUnitProperties.CONCURRENCY_MANAGER_BUILD_OBJECT_COMPLETE_WAIT_TIME, exception));
        }
    }

    private void updateConcurrencyManagerMaxAllowedSleepTime(Map persistenceProperties) {
        String maxAllowedSleepTime = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.CONCURRENCY_MANAGER_MAX_SLEEP_TIME, persistenceProperties, session);
        try {
            if (maxAllowedSleepTime != null) {
                ConcurrencyUtil.SINGLETON.setMaxAllowedSleepTime(Long.parseLong(maxAllowedSleepTime));
            }
        } catch (NumberFormatException exception) {
            this.session.handleException(ValidationException.invalidValueForProperty(maxAllowedSleepTime, PersistenceUnitProperties.CONCURRENCY_MANAGER_MAX_SLEEP_TIME, exception));
        }
    }

    private void updateConcurrencyManagerMaxAllowedFrequencyToProduceTinyDumpLogMessage(Map persistenceProperties) {
        String maxAllowedSleepTime = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.CONCURRENCY_MANAGER_MAX_FREQUENCY_DUMP_TINY_MESSAGE, persistenceProperties, session);
        try {
            if (maxAllowedSleepTime != null) {
                ConcurrencyUtil.SINGLETON.setMaxAllowedFrequencyToProduceTinyDumpLogMessage(Long.parseLong(maxAllowedSleepTime));
            }
        } catch (NumberFormatException exception) {
            this.session.handleException(ValidationException.invalidValueForProperty(maxAllowedSleepTime, PersistenceUnitProperties.CONCURRENCY_MANAGER_MAX_FREQUENCY_DUMP_TINY_MESSAGE, exception));
        }
    }

    private void updateConcurrencyManagerMaxAllowedFrequencyToProduceMassiveDumpLogMessage(Map persistenceProperties) {
        String maxAllowedSleepTime = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.CONCURRENCY_MANAGER_MAX_FREQUENCY_DUMP_MASSIVE_MESSAGE, persistenceProperties, session);
        try {
            if (maxAllowedSleepTime != null) {
                ConcurrencyUtil.SINGLETON.setMaxAllowedFrequencyToProduceMassiveDumpLogMessage(Long.parseLong(maxAllowedSleepTime));
            }
        } catch (NumberFormatException exception) {
            this.session.handleException(ValidationException.invalidValueForProperty(maxAllowedSleepTime, PersistenceUnitProperties.CONCURRENCY_MANAGER_MAX_FREQUENCY_DUMP_MASSIVE_MESSAGE, exception));
        }
    }

    private void updateConcurrencyManagerAllowInterruptedExceptionFired(Map persistenceProperties) {
        String allowInterruptedExceptionFired = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.CONCURRENCY_MANAGER_ALLOW_INTERRUPTED_EXCEPTION, persistenceProperties, session);
        try {
            if (allowInterruptedExceptionFired != null) {
                ConcurrencyUtil.SINGLETON.setAllowInterruptedExceptionFired(Boolean.parseBoolean(allowInterruptedExceptionFired));
            }
        } catch (NumberFormatException exception) {
            this.session.handleException(ValidationException.invalidValueForProperty(allowInterruptedExceptionFired, PersistenceUnitProperties.CONCURRENCY_MANAGER_ALLOW_INTERRUPTED_EXCEPTION, exception));
        }
    }

    private void updateConcurrencyManagerAllowConcurrencyExceptionToBeFiredUp(Map persistenceProperties) {
        String allowConcurrencyExceptionToBeFiredUp = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.CONCURRENCY_MANAGER_ALLOW_CONCURRENCY_EXCEPTION, persistenceProperties, session);
        try {
            if (allowConcurrencyExceptionToBeFiredUp != null) {
                ConcurrencyUtil.SINGLETON.setAllowConcurrencyExceptionToBeFiredUp(Boolean.parseBoolean(allowConcurrencyExceptionToBeFiredUp));
            }
        } catch (NumberFormatException exception) {
            this.session.handleException(ValidationException.invalidValueForProperty(allowConcurrencyExceptionToBeFiredUp, PersistenceUnitProperties.CONCURRENCY_MANAGER_ALLOW_CONCURRENCY_EXCEPTION, exception));
        }
    }

    private void updateConcurrencyManagerAllowTakingStackTraceDuringReadLockAcquisition(Map persistenceProperties) {
        String allowTakingStackTraceDuringReadLockAcquisition = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.CONCURRENCY_MANAGER_ALLOW_STACK_TRACE_READ_LOCK, persistenceProperties, session);
        try {
            if (allowTakingStackTraceDuringReadLockAcquisition != null) {
                ConcurrencyUtil.SINGLETON.setAllowTakingStackTraceDuringReadLockAcquisition(Boolean.parseBoolean(allowTakingStackTraceDuringReadLockAcquisition));
            }
        } catch (NumberFormatException exception) {
            this.session.handleException(ValidationException.invalidValueForProperty(allowTakingStackTraceDuringReadLockAcquisition, PersistenceUnitProperties.CONCURRENCY_MANAGER_ALLOW_STACK_TRACE_READ_LOCK, exception));
        }
    }

    private void updateConcurrencyManagerUseObjectBuildingSemaphore(Map persistenceProperties) {
        String useObjectBuildingSemaphore = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.CONCURRENCY_MANAGER_USE_SEMAPHORE_TO_SLOW_DOWN_OBJECT_BUILDING, persistenceProperties, session);
        try {
            if (useObjectBuildingSemaphore != null) {
                ConcurrencyUtil.SINGLETON.setUseSemaphoreInObjectBuilder(Boolean.parseBoolean(useObjectBuildingSemaphore));
            }
        } catch (NumberFormatException exception) {
            this.session.handleException(ValidationException.invalidValueForProperty(useObjectBuildingSemaphore, PersistenceUnitProperties.CONCURRENCY_MANAGER_USE_SEMAPHORE_TO_SLOW_DOWN_OBJECT_BUILDING, exception));
        }
    }

    private void updateConcurrencyManagerUseWriteLockManagerSemaphore(Map persistenceProperties) {
        String useWriteLockManagerSemaphore = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.CONCURRENCY_MANAGER_USE_SEMAPHORE_TO_SLOW_DOWN_WRITE_LOCK_MANAGER_ACQUIRE_REQUIRED_LOCKS, persistenceProperties, session);
        try {
            if (useWriteLockManagerSemaphore != null) {
                ConcurrencyUtil.SINGLETON.setUseSemaphoreToLimitConcurrencyOnWriteLockManagerAcquireRequiredLocks(Boolean.parseBoolean(useWriteLockManagerSemaphore));
            }
        } catch (NumberFormatException exception) {
            this.session.handleException(ValidationException.invalidValueForProperty(useWriteLockManagerSemaphore, PersistenceUnitProperties.CONCURRENCY_MANAGER_USE_SEMAPHORE_TO_SLOW_DOWN_WRITE_LOCK_MANAGER_ACQUIRE_REQUIRED_LOCKS, exception));
        }
    }

    private void updateConcurrencyManagerNoOfThreadsAllowedToObjectBuildInParallel(Map persistenceProperties) {
        String noOfThreadsAllowedToObjectBuildInParallel = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.CONCURRENCY_MANAGER_OBJECT_BUILDING_NO_THREADS, persistenceProperties, session);
        try {
            if (noOfThreadsAllowedToObjectBuildInParallel != null) {
                ConcurrencyUtil.SINGLETON.setNoOfThreadsAllowedToObjectBuildInParallel(Integer.parseInt(noOfThreadsAllowedToObjectBuildInParallel));
            }
        } catch (NumberFormatException exception) {
            this.session.handleException(ValidationException.invalidValueForProperty(noOfThreadsAllowedToObjectBuildInParallel, PersistenceUnitProperties.CONCURRENCY_MANAGER_OBJECT_BUILDING_NO_THREADS, exception));
        }
    }

    private void updateConcurrencyManagerNoOfThreadsAllowedToDoWriteLockManagerAcquireRequiredLocksInParallel(Map persistenceProperties) {
        String noOfThreadsAllowedToDoWriteLockManagerAcquireRequiredLocksInParallel = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.CONCURRENCY_MANAGER_WRITE_LOCK_MANAGER_ACQUIRE_REQUIRED_LOCKS_NO_THREADS, persistenceProperties, session);
        try {
            if (noOfThreadsAllowedToDoWriteLockManagerAcquireRequiredLocksInParallel != null) {
                ConcurrencyUtil.SINGLETON.setNoOfThreadsAllowedToDoWriteLockManagerAcquireRequiredLocksInParallel(Integer.parseInt(noOfThreadsAllowedToDoWriteLockManagerAcquireRequiredLocksInParallel));
            }
        } catch (NumberFormatException exception) {
            this.session.handleException(ValidationException.invalidValueForProperty(noOfThreadsAllowedToDoWriteLockManagerAcquireRequiredLocksInParallel, PersistenceUnitProperties.CONCURRENCY_MANAGER_WRITE_LOCK_MANAGER_ACQUIRE_REQUIRED_LOCKS_NO_THREADS, exception));
        }
    }

    private void updateConcurrencySemaphoreMaxTimePermit(Map persistenceProperties) {
        String concurrencySemaphoreMaxTimePermit = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.CONCURRENCY_SEMAPHORE_MAX_TIME_PERMIT, persistenceProperties, session);
        try {
            if (concurrencySemaphoreMaxTimePermit != null) {
                ConcurrencyUtil.SINGLETON.setConcurrencySemaphoreMaxTimePermit(Long.parseLong(concurrencySemaphoreMaxTimePermit));
            }
        } catch (NumberFormatException exception) {
            this.session.handleException(ValidationException.invalidValueForProperty(concurrencySemaphoreMaxTimePermit, PersistenceUnitProperties.CONCURRENCY_SEMAPHORE_MAX_TIME_PERMIT, exception));
        }
    }

    private void updateConcurrencySemaphoreLogTimeout(Map persistenceProperties) {
        String concurrencySemaphoreLogTimeout = getConfigPropertyAsStringLogDebug(PersistenceUnitProperties.CONCURRENCY_SEMAPHORE_LOG_TIMEOUT, persistenceProperties, session);
        try {
            if (concurrencySemaphoreLogTimeout != null) {
                ConcurrencyUtil.SINGLETON.setConcurrencySemaphoreLogTimeout(Long.parseLong(concurrencySemaphoreLogTimeout));
            }
        } catch (NumberFormatException exception) {
            this.session.handleException(ValidationException.invalidValueForProperty(concurrencySemaphoreLogTimeout, PersistenceUnitProperties.CONCURRENCY_SEMAPHORE_LOG_TIMEOUT, exception));
        }
    }
}