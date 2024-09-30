package core.framework.ddd.mongodb;

import core.framework.ddd.api.DomainEvent;
import core.framework.ddd.api.DomainEventBus;
import core.framework.ddd.mongodb.support.AggregateRootAfterDeleteEvent;
import core.framework.ddd.mongodb.support.AggregateRootBeforeDeleteEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.MongoMappingEvent;

import java.util.List;

/**
 * @author ebin
 */
public class MongodbDomainEventDispatcher extends AbstractMongoEventListener<AbstractAggregateRoot> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongodbDomainEventDispatcher.class);
    private final MongodbDomainEventStorageImpl mongodbDomainEventStore;
    private final DomainEventBus domainEventBus;

    public MongodbDomainEventDispatcher(MongodbDomainEventStorageImpl mongodbDomainEventStore, DomainEventBus domainEventBus) {
        this.mongodbDomainEventStore = mongodbDomainEventStore;
        this.domainEventBus = domainEventBus;
    }

    @Override
    public void onApplicationEvent(MongoMappingEvent<?> event) {
        super.onApplicationEvent(event);
        if (event instanceof AggregateRootBeforeDeleteEvent beforeDeleteEvent) {
            onBeforeDelete(beforeDeleteEvent);
        } else if (event instanceof AggregateRootAfterDeleteEvent afterDeleteEvent) {
            onAfterDelete(afterDeleteEvent);
        }
    }

    @Override
    public void onBeforeSave(BeforeSaveEvent<AbstractAggregateRoot> event) {
        mongodbDomainEventStore.persist(event.getSource());
    }

    @Override
    public void onAfterSave(AfterSaveEvent<AbstractAggregateRoot> event) {
        riseDomainEvent(event.getSource());
        cleanDomainEvent(event.getSource());
    }

    private void onBeforeDelete(AggregateRootBeforeDeleteEvent event) {
        mongodbDomainEventStore.persist(event.getSource());
        riseDomainEvent(event.getSource());
    }

    private void onAfterDelete(AggregateRootAfterDeleteEvent event) {
        riseDomainEvent(event.getSource());
        cleanDomainEvent(event.getSource());
    }

    private void riseDomainEvent(Object entity) {
        if (entity instanceof AbstractAggregateRoot aggregateRoot) {
            DomainEventHelper domainEventHelper = aggregateRoot.domainEventHelper();
            List<DomainEvent> domainEvents = domainEventHelper.getDomainEvents();
            for (DomainEvent domainEvent : domainEvents) {
                LOGGER.info("rise domain event: [{}]", domainEvent.toString());
                domainEventBus.dispatch(domainEvent);
            }
        }
    }

    private void cleanDomainEvent(Object entity) {
        if (entity instanceof AbstractAggregateRoot aggregateRoot) {
            aggregateRoot.domainEventHelper().clearDomainEvents();
        }
    }
}
