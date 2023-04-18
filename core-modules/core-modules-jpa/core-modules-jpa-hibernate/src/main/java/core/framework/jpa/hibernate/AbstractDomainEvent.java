package core.framework.jpa.hibernate;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.DomainEvent;

import java.time.ZonedDateTime;

/**
 * @author ebin
 */
public abstract class AbstractDomainEvent<T extends AggregateRoot<T, ID>, ID> implements DomainEvent<T, ID> {
    private final ID aggregateRootId;
    private final Class<T> aggregateRootClass;
    private final ZonedDateTime createdTime;

    public AbstractDomainEvent(T source) {
        this.aggregateRootId = source.getId();
        this.aggregateRootClass = (Class<T>) source.getClass();
        this.createdTime = ZonedDateTime.now();
    }

    @Override
    public ID getAggregateRootId() {
        return aggregateRootId;
    }

    @Override
    public Class<T> getAggregateRootClass() {
        return aggregateRootClass;
    }

    @Override
    public ZonedDateTime getCreatedTime() {
        return this.createdTime;
    }
}
