package core.framework.mongodb;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.DomainEvent;
import core.framework.ddd.DomainEventStore;
import core.framework.ddd.support.AbstractDomainEvent;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ebin
 */
public final class MongodbDomainEventStoreImpl implements DomainEventStore {
    private final MongoOperations mongoOperations;

    public MongodbDomainEventStoreImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public void persist(AggregateRoot<?, ?> aggregateRoot) {
        aggregateRoot.prepareDispatchDomainEvent();
        List<? extends DomainEvent<?, ?>> domainEvents = aggregateRoot.getDomainEvents();
        if (domainEvents.isEmpty()) {
            return;
        }
        List<DomainEventTracking> domainEventTrackings = new ArrayList<>(domainEvents.size());
        for (DomainEvent<?, ?> domainEvent : domainEvents) {
            if (domainEvent instanceof AbstractDomainEvent) {
                domainEventTrackings.add(new DomainEventTracking(aggregateRoot, domainEvent));
            }
        }
        mongoOperations.insertAll(domainEventTrackings);
    }
}
