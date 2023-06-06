package core.framework.jpa.eclipselink.common;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.TargetDatabase;
import org.eclipse.persistence.internal.jpa.IsolatedHashMap;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.logging.SessionLog;

import java.util.Map;

import static org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider.getConfigPropertyAsString;

/**
 * @author ebin
 */
public class EntityManagerFactoryProvider {
    public static final Map<String, DDDSupportEntityManagerSetupImpl> emSetupImpls = IsolatedHashMap.newMap();

    // TEMPORARY - WILL BE REMOVED.
    // Used to warn users about deprecated property name and suggest the valid name.
    // TEMPORARY the old property names will be translated to the new ones and processed.
    protected static final String oldPropertyNames[][] = {
        {PersistenceUnitProperties.CONNECTION_POOL_MAX, "eclipselink.max-write-connections"},
        {PersistenceUnitProperties.CONNECTION_POOL_MIN, "eclipselink.min-write-connections"},
        {PersistenceUnitProperties.CONNECTION_POOL_MAX, "eclipselink.max-read-connections"},
        {PersistenceUnitProperties.CONNECTION_POOL_MIN, "eclipselink.min-read-connections"},
        {PersistenceUnitProperties.CONNECTION_POOL_MAX, "eclipselink.connections.max"},
        {PersistenceUnitProperties.CONNECTION_POOL_MIN, "eclipselink.connections.min"},
        {PersistenceUnitProperties.CONNECTION_POOL_INITIAL, "eclipselink.connections.initial"},
        {PersistenceUnitProperties.CONNECTION_POOL_WAIT, "eclipselink.connections.wait"},
        {PersistenceUnitProperties.CONNECTION_POOL_MAX, "eclipselink.write-connections.max"},
        {PersistenceUnitProperties.CONNECTION_POOL_MIN, "eclipselink.write-connections.min"},
        {PersistenceUnitProperties.CONNECTION_POOL_INITIAL, "eclipselink.write-connections.initial"},
        {PersistenceUnitProperties.CONNECTION_POOL_MAX, "eclipselink.read-connections.max"},
        {PersistenceUnitProperties.CONNECTION_POOL_MIN, "eclipselink.read-connections.min"},
        {PersistenceUnitProperties.CONNECTION_POOL_SHARED, "eclipselink.read-connections.shared"},
        {PersistenceUnitProperties.CONNECTION_POOL_INITIAL, "eclipselink.read-connections.initial"},
        {PersistenceUnitProperties.JDBC_BIND_PARAMETERS, "eclipselink.bind-all-parameters"},
        {PersistenceUnitProperties.TARGET_DATABASE, "eclipselink.platform.class.name"},
        {PersistenceUnitProperties.TARGET_SERVER, "eclipselink.server.platform.class.name"},
        {PersistenceUnitProperties.CACHE_SIZE_DEFAULT, "eclipselink.cache.default-size"},
        {PersistenceUnitProperties.JDBC_USER, "eclipselink.jdbc.user"},
        {PersistenceUnitProperties.JDBC_DRIVER, "eclipselink.jdbc.driver"},
        {PersistenceUnitProperties.JDBC_URL, "eclipselink.jdbc.url"},
        {PersistenceUnitProperties.JDBC_PASSWORD, "eclipselink.jdbc.password"},
        {PersistenceUnitProperties.WEAVING, "persistence.tools.weaving"},
        {PersistenceUnitProperties.LOGGING_LEVEL + "." + SessionLog.METAMODEL, PersistenceUnitProperties.LOGGING_LEVEL + ".jpa_" + SessionLog.METAMODEL},
        {PersistenceUnitProperties.LOGGING_LEVEL + "." + SessionLog.METADATA, PersistenceUnitProperties.LOGGING_LEVEL + ".ejb_or_" + SessionLog.METADATA}
    };

    /**
     * Default constructor to allow reflection.
     */
    public EntityManagerFactoryProvider() {
    }

    /**
     * Add an EntityManagerSetupImpl to the cached list
     * These are used to ensure all persistence units that are the same get the same underlying session
     */
    public static void addEntityManagerSetupImpl(String name, DDDSupportEntityManagerSetupImpl setup) {
        synchronized (EntityManagerFactoryProvider.emSetupImpls) {
            if (name == null) {
                EntityManagerFactoryProvider.emSetupImpls.put("", setup);
            }
            EntityManagerFactoryProvider.emSetupImpls.put(name, setup);
        }
    }

    protected static String getConfigPropertyAsStringLogDebug(String propertyKey, Map overrides, AbstractSession session) {
        return (String) getConfigPropertyLogDebug(propertyKey, overrides, session);
    }

    protected static String getConfigPropertyAsStringLogDebug(String propertyKey, Map overrides, AbstractSession session, boolean useSystemAsDefault) {
        return (String) getConfigPropertyLogDebug(propertyKey, overrides, session, useSystemAsDefault);
    }

    protected static String getConfigPropertyAsStringLogDebug(String propertyKey, Map overrides, String defaultValue, AbstractSession session) {
        String value = getConfigPropertyAsStringLogDebug(propertyKey, overrides, session);
        if (value == null) {
            value = defaultValue;
            session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "property_value_default", new Object[]{propertyKey, value});
        }
        return value;
    }

    protected static Object getConfigPropertyLogDebug(String propertyKey, Map overrides, AbstractSession session) {
        return getConfigPropertyLogDebug(propertyKey, overrides, session, true);
    }

    protected static Object getConfigPropertyLogDebug(String propertyKey, Map overrides, AbstractSession session, boolean useSystemAsDefault) {
        Object value = null;
        if (overrides != null) {
            value = overrides.get(propertyKey);
        }
        if ((value == null) && useSystemAsDefault) {
            value = System.getProperty(propertyKey);
        }
        if ((value != null) && (session != null)) {
            if (session.shouldLog(SessionLog.FINEST, SessionLog.PROPERTIES)) {
                String overrideValue = PersistenceUnitProperties.getOverriddenLogStringForProperty(propertyKey);
                ;
                Object logValue = (overrideValue == null) ? value : overrideValue;
                session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "property_value_specified", new Object[]{propertyKey, logValue});
            }
        }

        return value;
    }

    protected static Object getConfigProperty(String propertyKey, Map overrides) {
        return getConfigProperty(propertyKey, overrides, true);
    }

    protected static Object getConfigProperty(String propertyKey, Map overrides, boolean useSystemAsDefault) {
        Object value = null;
        if (overrides != null) {
            value = overrides.get(propertyKey);
        }
        if ((value == null) && useSystemAsDefault) {
            value = System.getProperty(propertyKey);
        }

        return value;
    }

    protected static Object getConfigProperty(String propertyKey, Map overrides, Object defaultObj) {
        Object obj = getConfigProperty(propertyKey, overrides);
        return (obj == null) ? defaultObj : obj;
    }

    /**
     * Return the setup class for a given entity manager name
     */
    public static DDDSupportEntityManagerSetupImpl getEntityManagerSetupImpl(String emName) {
        synchronized (EntityManagerFactoryProvider.emSetupImpls) {
            if (emName == null) {
                return EntityManagerFactoryProvider.emSetupImpls.get("");
            }
            return EntityManagerFactoryProvider.emSetupImpls.get(emName);
        }
    }

    public static Map<String, DDDSupportEntityManagerSetupImpl> getEmSetupImpls() {
        return emSetupImpls;
    }

    /**
     * Logs in to given session. If user has not specified  <code>TARGET_DATABASE</code>
     * the platform would be auto detected
     *
     * @param session    The session to login to.
     * @param properties User specified properties for the persistence unit
     */
    protected static void login(DatabaseSessionImpl session, Map properties, boolean requiresConnection) {
        String databaseGenerationAction = getConfigPropertyAsString(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION, properties);

        // Avoid an actual connection if we don't need one. If the user provides
        // us with a user name and password we will connect. At minimum if they
        // provide the platform we'll generate the DDL as if we had connected.
        if ((databaseGenerationAction == null || databaseGenerationAction.equals(PersistenceUnitProperties.SCHEMA_GENERATION_NONE_ACTION)) && !requiresConnection) {
            session.setDatasourceAndInitialize();
        } else {
            String eclipselinkPlatform = (String) properties.get(PersistenceUnitProperties.TARGET_DATABASE);
            if (eclipselinkPlatform == null || eclipselinkPlatform.equals(TargetDatabase.Auto) || session.isBroker()) {
                // if user has not specified a database platform, try to detect.
                // Will also look for jpa 2.1 schema properties.
                session.loginAndDetectDatasource();
            } else {
                session.login();
            }
        }
    }


    /**
     * This is a TEMPORARY method that will be removed.
     * DON'T USE THIS METHOD - for internal use only.
     */
    protected static void translateOldProperties(Map m, AbstractSession session) {
        for (int i = 0; i < oldPropertyNames.length; i++) {
            Object value = getConfigPropertyAsString(oldPropertyNames[i][1], m);
            if (value != null) {
                if (session != null) {
                    session.log(SessionLog.INFO, SessionLog.TRANSACTION, "deprecated_property", oldPropertyNames[i]);
                }
                m.put(oldPropertyNames[i][0], value);
            }
        }
    }

    protected static void warnOldProperties(Map m, AbstractSession session) {
        for (int i = 0; i < oldPropertyNames.length; i++) {
            Object value = m.get(oldPropertyNames[i][1]);
            if (value != null) {
                session.log(SessionLog.INFO, SessionLog.TRANSACTION, "deprecated_property", oldPropertyNames[i]);
            }
        }
    }
}
