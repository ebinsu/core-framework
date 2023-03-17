package core.framework.jpa.hibernate;

import core.framework.json.JSON;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * @author ebin
 */
@Entity
@Table(name = "domain_event_tracking")
public class DomainEventTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

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

    @Column(name = "payload")
    private String payload;

    @NotNull
    @Column(name = "created_time")
    private ZonedDateTime createdTime;

    private DomainEventTracking() {
    }

    public DomainEventTracking(AbstractDomainEvent<?> event) {
        this.eventName = event.getClass().getName();
        this.aggregateRootClass = event.getSource().getClass().getTypeName();
        this.aggregateRootId = String.valueOf(event.getSource().getId());
        this.createdTime = ZonedDateTime.now();
        if (Objects.nonNull(event.getPayload())) {
            this.payload = JSON.toJSON(event.getPayload());
        }
        if (Objects.nonNull(event.getSource())) {
            this.aggregateRootSnapshot = JSON.toJSON(event.getSource());
        }
    }

    public Long getId() {
        return this.id;
    }

    public ZonedDateTime getCreatedTime() {
        return this.createdTime;
    }

    public <T> T getPayload(Class<T> instanceClass) {
        return JSON.fromJSON(instanceClass, this.payload);
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
