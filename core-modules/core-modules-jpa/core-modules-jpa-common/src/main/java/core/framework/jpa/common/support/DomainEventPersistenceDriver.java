package core.framework.jpa.common.support;

import core.framework.ddd.AggregateRoot;
import jakarta.persistence.EntityManager;

/**
 * @author ebin
 */
public interface DomainEventPersistenceDriver {
    void persist(AggregateRoot<?, ?> aggregateRoot, EntityManager entityManager);

    String getPersistenceUnitName();
}
