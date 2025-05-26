package core.framework.ddd.common.event;

import com.google.common.base.CaseFormat;
import core.framework.ddd.api.AggregateRoot;
import core.framework.ddd.api.AggregateRootMetadata;
import core.framework.ddd.api.DomainEvent;
import core.framework.json.JSON;

import java.time.ZonedDateTime;

/**
 * @author ebin
 */
public abstract class AbstractDomainEvent implements DomainEvent {
    private final ZonedDateTime createdTime = ZonedDateTime.now();
    private AggregateRootMetadata aggregateRootMetadata;

    protected AbstractDomainEvent() {
    }

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

    @Override
    public String description() {
        return "handle-" + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, this.getClass().getSimpleName());
    }
}
