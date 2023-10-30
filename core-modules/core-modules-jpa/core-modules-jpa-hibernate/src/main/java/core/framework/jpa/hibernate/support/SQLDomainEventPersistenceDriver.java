package core.framework.jpa.hibernate.support;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.DomainEvent;
import core.framework.jpa.common.support.DomainEventPersistenceDriver;
import core.framework.jpa.hibernate.configuration.HibernateConfiguration;
import core.framework.jpa.hibernate.DomainEventTracking;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * @author ebin
 */
public class SQLDomainEventPersistenceDriver implements DomainEventPersistenceDriver {
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
        return HibernateConfiguration.PERSISTENCE_UNIT_INFO_NAME;
    }
}
