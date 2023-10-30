package core.framework.jpa.hibernate.support;


import core.framework.jpa.common.support.ConfigurablePersistenceUnitInfo;

/**
 * @author ebin
 */
@FunctionalInterface
public interface ConfigurablePersistenceUnitCustomizer {
    void customize(ConfigurablePersistenceUnitInfo persistenceUnitInfo);
}
