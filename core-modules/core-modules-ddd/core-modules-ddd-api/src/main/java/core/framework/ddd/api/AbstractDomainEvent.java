package core.framework.ddd.api;

import core.framework.json.JSON;

import java.time.ZonedDateTime;

/**
 * @author ebin
 */
public abstract non-sealed class AbstractDomainEvent implements DomainEvent {
    private AggregateRootMetadata aggregateRootMetadata;
    private final ZonedDateTime createdTime = ZonedDateTime.now();

    @Override
    public AggregateRootMetadata getAggregateRootMetadata() {
        return this.aggregateRootMetadata;
    }

    @Override
    public ZonedDateTime getCreatedTime() {
        return this.createdTime;
    }

    @Override
    public String toString() {
        return JSON.toJSON(this);
    }

    public void setAggregateRootMetadata(AggregateRoot aggregateRoot) {
        this.aggregateRootMetadata = new AggregateRootMetadata(aggregateRoot);
    }
}
