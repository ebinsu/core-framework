package core.framework.jpa.hibernate;

import core.framework.ddd.AggregateRoot;

/**
 * @author ebin
 */
public class DomainEventAttachment<A extends AggregateRoot<A, ID>, ID> {
    private final ID id;
    private final Class<A> aggregateRootClass;

    public DomainEventAttachment(AggregateRoot<A, ID> aggregateRoot) {
        this.id = aggregateRoot.getId();
        this.aggregateRootClass = (Class<A>) aggregateRoot.getClass();
    }

    public ID getAggregateRootId() {
        return this.id;
    }

    public Class<A> getAggregateRootClass() {
        return aggregateRootClass;
    }
}