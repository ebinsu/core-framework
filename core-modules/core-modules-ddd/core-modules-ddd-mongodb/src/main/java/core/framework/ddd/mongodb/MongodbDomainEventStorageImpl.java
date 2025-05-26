package core.framework.ddd.mongodb;

import core.framework.ddd.common.event.AbstractDomainEvent;
import core.framework.ddd.api.AggregateRoot;
import core.framework.ddd.api.DomainEvent;
import core.framework.ddd.api.DomainEventStorage;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author ebin
 */
public final class MongodbDomainEventStorageImpl implements DomainEventStorage {
    private final MongoOperations mongoOperations;

    public MongodbDomainEventStorageImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

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

        List<DomainEventTracking> domainEventTrackings = new ArrayList<>(domainEvents.size());
        for (DomainEvent domainEvent : domainEvents) {
            if (domainEvent instanceof AbstractDomainEvent ade) {
                ade.setAggregateRootMetadata(aggregateRoot);
                DomainEventTracking domainEventTracking = new DomainEventTracking(aggregateRoot, ade);
                domainEventTrackings.add(domainEventTracking);
            }
        }
        mongoOperations.insertAll(domainEventTrackings);
    }
}
