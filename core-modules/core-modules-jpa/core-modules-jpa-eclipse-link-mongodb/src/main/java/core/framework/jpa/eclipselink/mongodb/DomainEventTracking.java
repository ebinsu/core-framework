package core.framework.jpa.eclipselink.mongodb;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.DomainEvent;
import core.framework.json.JSON;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.Field;
import org.eclipse.persistence.nosql.annotations.NoSql;

import java.time.ZonedDateTime;

/**
 * @author ebin
 */
@Entity
@Table(name = "domain_event_tracking")
@NoSql(dataFormat = DataFormatType.MAPPED)
public class DomainEventTracking {
    @Id
    @GeneratedValue
    @Field(name = "_id")
    private String id;

    @NotNull
    @Column(name = "event_name")
    private String eventName;

    @NotNull
    @Column(name = "aggregate_root_class")
    private String aggregateRootClass;

    @NotNull
    @Column(name = "aggregate_root_id")
    private String aggregateRootId;

    @Column(name = "aggregate_root_snapshot", columnDefinition = "TEXT")
    private String aggregateRootSnapshot;

    @Column(name = "domain_event_snapshot", columnDefinition = "TEXT")
    private String domainEventSnapshot;

    @NotNull
    @Column(name = "created_time")
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

    public String getId() {
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
