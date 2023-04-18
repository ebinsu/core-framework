package core.framework.jpa.hibernate.support;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.DomainEvent;
import core.framework.ddd.support.DomainEventBus;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.FlushEntityEvent;
import org.hibernate.event.spi.FlushEntityEventListener;

import java.util.List;

/**
 * @author ebin
 */
public class HibernatePreCommitEventDispatcher implements FlushEntityEventListener {
    @Override
    public void onFlushEntity(FlushEntityEvent event) throws HibernateException {
        if (event.getEntity() instanceof AggregateRoot<?, ?> aggregateRoot) {
            HibernateDomainEventStore.INSTANCE.persist(aggregateRoot);
            riseDomainEvent(aggregateRoot);
        }
    }

    private void riseDomainEvent(AggregateRoot<?, ?> aggregateRoot) {
        List<? extends DomainEvent<?, ?>> domainEvents = aggregateRoot.getDomainEvents();
        for (DomainEvent<?, ?> domainEvent : domainEvents) {
            DomainEventBus.INSTANCE.publishPreCommitEvent(domainEvent);
        }
    }
}
