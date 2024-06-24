package core.framework.jpa.hibernate.support;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author ebin
 */
public class HibernatePostCommitEventDispatcher implements PostCommitInsertEventListener, PostCommitUpdateEventListener, PostCommitDeleteEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernatePostCommitEventDispatcher.class);

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
        if (entity instanceof AggregateRoot aggregateRoot) {
            List<DomainEvent> domainEvents = aggregateRoot.getDomainEvents();
            for (DomainEvent domainEvent : domainEvents) {
                LOGGER.info("rise domain event: [{}]", domainEvent.toString());
                DomainEventBusHolder.get().dispatch(domainEvent, Trigger.AFTER_COMMIT);
            }
        }
    }

    private void cleanDomainEvent(Object entity) {
        if (entity instanceof AggregateRoot aggregateRoot) {
            aggregateRoot.clearDomainEvents();
        }
    }
}
