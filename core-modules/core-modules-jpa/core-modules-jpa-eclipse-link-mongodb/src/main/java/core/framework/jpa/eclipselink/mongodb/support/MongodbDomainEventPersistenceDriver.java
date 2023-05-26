package core.framework.jpa.eclipselink.mongodb.support;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.DomainEvent;
import core.framework.jpa.common.support.DomainEventPersistenceDriver;
import core.framework.jpa.eclipselink.mongodb.DomainEventTracking;
import core.framework.jpa.eclipselink.mongodb.configuration.MongodbConfiguration;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * @author ebin
 */
public class MongodbDomainEventPersistenceDriver implements DomainEventPersistenceDriver {
    @Override
    public void persist(AggregateRoot<?, ?> aggregateRoot, EntityManager entityManager) {
        List<? extends DomainEvent<?, ?>> domainEvents = aggregateRoot.getDomainEvents();
        for (DomainEvent<?, ?> event : domainEvents) {
            DomainEventTracking domainEventTracking = new DomainEventTracking(aggregateRoot, event);
            entityManager.persist(domainEventTracking);
        }
    }

    @Override
    public String getPersistenceUnitName() {
        return MongodbConfiguration.MONGODB_PERSISTENCE_UNIT_INFO_NAME;
    }
}
