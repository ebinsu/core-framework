package core.framework.ddd.mongodb;

import core.framework.ddd.api.AggregateRoot;
import core.framework.ddd.api.DomainEvent;
import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.ZonedDateTime;

/**
 * @author ebin
 */
@Document("domain_event_tracking")
public class DomainEventTracking {
    @MongoId
    private ObjectId id;

    @NotNull
    @Field(name = "event_name")
    private String eventName;

    @NotNull
    @Field(name = "aggregate_root_class")
    private String aggregateRootClass;

    @NotNull
    @Field(name = "aggregate_root_id")
    private String aggregateRootId;

    @Field(name = "aggregate_root_snapshot")
    private AggregateRoot aggregateRootSnapshot;

    @Field(name = "domain_event_snapshot")
    private DomainEvent domainEventSnapshot;

    @NotNull
    @Field(name = "created_time")
    private ZonedDateTime createdTime;

    private DomainEventTracking() {
    }

    public DomainEventTracking(AggregateRoot aggregateRoot, DomainEvent event) {
        this.eventName = event.getClass().getName();
        this.aggregateRootClass = event.getAggregateRootMetadata().getType();
        this.aggregateRootId = String.valueOf(event.getAggregateRootMetadata().getId());
        this.createdTime = ZonedDateTime.now();
        this.domainEventSnapshot = event;
        this.aggregateRootSnapshot = aggregateRoot;
    }

    public ObjectId getId() {
        return this.id;
    }

    public ZonedDateTime getCreatedTime() {
        return this.createdTime;
    }

    public String getAggregateRootClass() {
        return aggregateRootClass;
    }

    public String getEventName() {
        return eventName;
    }

    public String getAggregateRootId() {
        return aggregateRootId;
    }

    public AggregateRoot getAggregateRootSnapshot() {
        return aggregateRootSnapshot;
    }

    public DomainEvent getDomainEventSnapshot() {
        return domainEventSnapshot;
    }
}
