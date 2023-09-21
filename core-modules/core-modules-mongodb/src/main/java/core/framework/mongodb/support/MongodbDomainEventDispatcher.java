package core.framework.mongodb.support;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.DomainEvent;
import core.framework.ddd.annotation.Trigger;
import core.framework.ddd.support.DomainEventBusHolder;
import core.framework.mongodb.AbstractAggregateRoot;
import core.framework.mongodb.MongodbDomainEventStoreImpl;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.MongoMappingEvent;

import java.util.List;

/**
 * @author ebin
 */
public class MongodbDomainEventDispatcher extends AbstractMongoEventListener<AbstractAggregateRoot<?, ?>> {
    private final MongodbDomainEventStoreImpl mongodbDomainEventStore;

    public MongodbDomainEventDispatcher(MongodbDomainEventStoreImpl mongodbDomainEventStore) {
        this.mongodbDomainEventStore = mongodbDomainEventStore;
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
    public void onBeforeSave(BeforeSaveEvent<AbstractAggregateRoot<?, ?>> event) {
        mongodbDomainEventStore.persist(event.getSource());
        riseDomainEvent(event.getSource(), Trigger.BEFORE_COMMIT);
    }

    @Override
    public void onAfterSave(AfterSaveEvent<AbstractAggregateRoot<?, ?>> event) {
        riseDomainEvent(event.getSource(), Trigger.AFTER_COMMIT);
        cleanDomainEvent(event.getSource());
    }

    private void onBeforeDelete(AggregateRootBeforeDeleteEvent event) {
        mongodbDomainEventStore.persist(event.getSource());
        riseDomainEvent(event.getSource(), Trigger.BEFORE_COMMIT);
    }

    private void onAfterDelete(AggregateRootAfterDeleteEvent event) {
        riseDomainEvent(event.getSource(), Trigger.AFTER_COMMIT);
        cleanDomainEvent(event.getSource());
    }

    private void riseDomainEvent(AggregateRoot<?, ?> aggregateRoot, Trigger trigger) {
        List<? extends DomainEvent<?, ?>> domainEvents = aggregateRoot.getDomainEvents();
        for (DomainEvent<?, ?> domainEvent : domainEvents) {
            DomainEventBusHolder.get().dispatch(domainEvent, trigger);
        }
    }

    private void cleanDomainEvent(Object entity) {
        if (entity instanceof AggregateRoot<?, ?> aggregateRoot) {
            aggregateRoot.clearDomainEvents();
        }
    }
}
