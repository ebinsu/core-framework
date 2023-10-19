package core.framework.jpa.hibernate.mysql.support;


import core.framework.jpa.common.support.ConfigurablePersistenceUnitInfo;

/**
 * @author ebin
 */
@FunctionalInterface
public interface MysqlPersistenceUnitCustomizer {
    void customize(ConfigurablePersistenceUnitInfo persistenceUnitInfo);
}
