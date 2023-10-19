package core.framework.jpa.hibernate.mysql.support;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.DomainEvent;
import core.framework.ddd.annotation.Trigger;
import core.framework.ddd.support.DomainEventBusHolder;
import org.hibernate.event.spi.PostCommitDeleteEventListener;
import org.hibernate.event.spi.PostCommitInsertEventListener;
import org.hibernate.event.spi.PostCommitUpdateEventListener;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.persister.entity.EntityPersister;

import java.util.List;

/**
 * @author ebin
 */
public class HibernatePostCommitEventDispatcher implements PostCommitInsertEventListener, PostCommitUpdateEventListener, PostCommitDeleteEventListener {
    @Override
    public void onPostDeleteCommitFailed(PostDeleteEvent event) {
        cleanDomainEvent(event.getEntity());
    }

    @Override
    public void onPostInsertCommitFailed(PostInsertEvent event) {
        cleanDomainEvent(event.getEntity());
    }

    @Override
    public void onPostUpdateCommitFailed(PostUpdateEvent event) {
        cleanDomainEvent(event.getEntity());
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        riseDomainEvent(event.getEntity());
        cleanDomainEvent(event.getEntity());
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        riseDomainEvent(event.getEntity());
        cleanDomainEvent(event.getEntity());
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        riseDomainEvent(event.getEntity());
        cleanDomainEvent(event.getEntity());
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {
        return true;
    }

    private void riseDomainEvent(Object entity) {
        if (entity instanceof AggregateRoot<?, ?> aggregateRoot) {
            List<? extends DomainEvent<?, ?>> domainEvents = aggregateRoot.getDomainEvents();
            for (DomainEvent<?, ?> domainEvent : domainEvents) {
                DomainEventBusHolder.get().dispatch(domainEvent, Trigger.AFTER_COMMIT);
            }
        }
    }

    private void cleanDomainEvent(Object entity) {
        if (entity instanceof AggregateRoot<?, ?> aggregateRoot) {
            aggregateRoot.clearDomainEvents();
        }
    }
}
