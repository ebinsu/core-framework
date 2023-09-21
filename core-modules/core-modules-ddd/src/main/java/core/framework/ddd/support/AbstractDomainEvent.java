package core.framework.ddd.support;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.DomainEvent;

import java.time.ZonedDateTime;

/**
 * @author ebin
 */
public abstract class AbstractDomainEvent<T extends AggregateRoot<T, ID>, ID> implements DomainEvent<T, ID> {
    private ID aggregateRootId;
    private Class<T> aggregateRootClass;
    private ZonedDateTime createdTime;

    public AbstractDomainEvent() {

    }

    @Override
    public final ID getAggregateRootId() {
        return aggregateRootId;
    }

    @Override
    public final Class<T> getAggregateRootClass() {
        return aggregateRootClass;
    }

    @Override
    public final ZonedDateTime getCreatedTime() {
        return this.createdTime;
    }

    @Override
    public final void prepareDispatch(T source) {
        this.aggregateRootId = source.getId();
        this.aggregateRootClass = (Class<T>) source.getClass();
        this.createdTime = ZonedDateTime.now();
        addAttachment(source);
    }

    protected void addAttachment(T source) {

    }
}
