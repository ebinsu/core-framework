package core.framework.ddd.hibernate;

import core.framework.ddd.api.AggregateRoot;
import core.framework.ddd.api.DomainEvent;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

/**
 * @author ebin
 */
@MappedSuperclass
public abstract class AbstractAggregateRoot extends AbstractEntity implements AggregateRoot {
    @Transient
    private final transient DomainEventHelper domainEventHelper = new DomainEventHelper();

    protected AbstractAggregateRoot() {
        this(null);
    }

    protected AbstractAggregateRoot(String createdBy) {
        super(createdBy);
    }

    @Override
    public void registerEvent(DomainEvent event) {
        domainEventHelper.registerEvent(event);
    }

    DomainEventHelper domainEventHelper() {
        return domainEventHelper;
    }
}
