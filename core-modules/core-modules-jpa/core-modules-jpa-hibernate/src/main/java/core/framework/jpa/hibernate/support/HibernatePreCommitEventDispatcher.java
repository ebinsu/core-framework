package core.framework.jpa.hibernate.support;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.DomainEvent;
import core.framework.ddd.support.DomainEventBus;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;

import java.util.List;

/**
 * @author ebin
 */
public class HibernatePreCommitEventDispatcher implements PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        handleEntity(event.getEntity());
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        handleEntity(event.getEntity());
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        handleEntity(event.getEntity());
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {
        return true;
    }

    private void handleEntity(Object entity) {
        if (entity instanceof AggregateRoot<?> aggregateRoot) {
            HibernateDomainEventStore.INSTANCE.persist(aggregateRoot);
            riseDomainEvent(aggregateRoot);
        }
    }

    private void riseDomainEvent(AggregateRoot<?> aggregateRoot) {
        List<? extends DomainEvent<?>> domainEvents = aggregateRoot.getDomainEvents();
        for (DomainEvent<?> domainEvent : domainEvents) {
            DomainEventBus.INSTANCE.publishPreCommitEvent(domainEvent);
        }
    }

}
