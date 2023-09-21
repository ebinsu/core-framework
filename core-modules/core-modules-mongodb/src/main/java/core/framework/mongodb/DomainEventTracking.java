package core.framework.mongodb;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.DomainEvent;
import core.framework.json.JSON;
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
    private String aggregateRootSnapshot;

    @Field(name = "domain_event_snapshot")
    private String domainEventSnapshot;

    @NotNull
    @Field(name = "created_time")
    private ZonedDateTime createdTime;

    private DomainEventTracking() {
    }

    public DomainEventTracking(AggregateRoot<?, ?> aggregateRoot, DomainEvent<?, ?> event) {
        this.eventName = event.getClass().getName();
        this.aggregateRootClass = event.getAggregateRootClass().getTypeName();
        this.aggregateRootId = String.valueOf(event.getAggregateRootId());
        this.createdTime = ZonedDateTime.now();
        this.domainEventSnapshot = JSON.toJSON(event);
        this.aggregateRootSnapshot = JSON.toJSON(aggregateRoot);
    }

    public ObjectId getId() {
        return this.id;
    }

    public ZonedDateTime getCreatedTime() {
        return this.createdTime;
    }

    public String getDomainEventSnapshot() {
        return domainEventSnapshot;
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

    public String getAggregateRootSnapshot() {
        return aggregateRootSnapshot;
    }
}
