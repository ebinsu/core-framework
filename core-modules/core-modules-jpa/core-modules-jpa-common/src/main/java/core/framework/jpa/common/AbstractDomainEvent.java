package core.framework.jpa.common;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.DomainEvent;

import java.time.ZonedDateTime;

/**
 * @author ebin
 */
public abstract class AbstractDomainEvent<T extends AggregateRoot<T, ID>, ID> implements DomainEvent<T, ID> {
    private final T source;

    public AbstractDomainEvent(T source) {
        this.source = source;
    }

    @Override
    public ID getAggregateRootId() {
        return this.source.getId();
    }

    @Override
    public Class<T> getAggregateRootClass() {
        return (Class<T>) this.source.getClass();
    }

    @Override
    public ZonedDateTime getCreatedTime() {
        return this.source.getCreatedTime();
    }
}
