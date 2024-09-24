package core.framework.ddd.hibernate.configuration;


import core.framework.ddd.hibernate.internal.ConfigurablePersistenceUnitInfo;

/**
 * @author ebin
 */
@FunctionalInterface
public interface ConfigurablePersistenceUnitCustomizer {
    void customize(ConfigurablePersistenceUnitInfo persistenceUnitInfo);
}
