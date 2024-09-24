package core.framework.ddd.hibernate;

import core.framework.ddd.api.AbstractDomainEvent;
import core.framework.ddd.api.AggregateRoot;
import core.framework.ddd.api.DomainEvent;
import core.framework.ddd.api.DomainEventStorage;
import core.framework.ddd.hibernate.internal.EntityManagerHolder;
import jakarta.persistence.EntityManager;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.FlushEntityEvent;
import org.hibernate.event.spi.FlushEntityEventListener;

import java.util.List;
import java.util.Objects;

/**
 * @author ebin
 */
public class DomainEventStorageImpl implements DomainEventStorage, FlushEntityEventListener {

    @Override
    public void persist(AggregateRoot aggregateRoot) {
        if (Objects.isNull(aggregateRoot.getId())) {
            throw new Error("The aggregateRoot is incomplete because the id is null.");
        }
        List<DomainEvent> domainEvents = null;
        if (aggregateRoot instanceof AbstractAggregateRoot abstractAggregateRoot) {
            DomainEventHelper domainEventHelper = abstractAggregateRoot.domainEventHelper();
            domainEvents = domainEventHelper.getDomainEvents();
        }

        if (domainEvents == null || domainEvents.isEmpty()) {
            return;
        }

        String persistenceUnitName = EntityManagerHolder.INSTANCE.getPersistenceUnitName(aggregateRoot.getClass());
        if (persistenceUnitName != null) {
            EntityManager entityManager = EntityManagerHolder.INSTANCE.get(persistenceUnitName);
            for (DomainEvent domainEvent : domainEvents) {
                if (domainEvent instanceof AbstractDomainEvent ade) {
                    ade.setAggregateRootMetadata(aggregateRoot);
                    DomainEventTracking domainEventTracking = new DomainEventTracking(aggregateRoot, ade);
                    entityManager.persist(domainEventTracking);
                }
            }
        }
    }

    @Override
    public void onFlushEntity(FlushEntityEvent event) throws HibernateException {
        if (event.getEntity() instanceof AggregateRoot aggregateRoot) {
            this.persist(aggregateRoot);
        }
    }
}
