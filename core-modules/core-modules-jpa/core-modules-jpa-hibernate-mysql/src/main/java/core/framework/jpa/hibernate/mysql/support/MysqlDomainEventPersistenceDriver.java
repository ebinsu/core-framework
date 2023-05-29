package core.framework.jpa.hibernate.mysql.support;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.DomainEvent;
import core.framework.jpa.common.support.DomainEventPersistenceDriver;
import core.framework.jpa.hibernate.mysql.DomainEventTracking;
import core.framework.jpa.hibernate.mysql.configuration.HibernateConfiguration;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * @author ebin
 */
public class MysqlDomainEventPersistenceDriver implements DomainEventPersistenceDriver {
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
        return HibernateConfiguration.MYSQL_PERSISTENCE_UNIT_INFO_NAME;
    }
}
