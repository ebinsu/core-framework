package core.framework.jpa.eclipselink.mongodb;

import core.framework.ddd.AggregateRoot;
import core.framework.jpa.common.support.DomainEventPersistenceDriver;
import jakarta.persistence.EntityManager;

/**
 * @author ebin
 */
public class MongodbDomainEventPersistenceDriver implements DomainEventPersistenceDriver {
    @Override
    public void persist(AggregateRoot<?, ?> aggregateRoot, EntityManager entityManager) {

    }

    @Override
    public String getPersistenceUnitName() {
        return MongodbConfig.MONGODB_PERSISTENCE_UNIT_INFO_NAME;
    }
}
