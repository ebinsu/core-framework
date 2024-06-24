package core.framework.test.hibernate.domain;


import core.framework.ddd.AbstractDomainEvent;
import core.framework.ddd.AggregateRoot;

/**
 * @author ebin
 */
public class TestDomainEvent extends AbstractDomainEvent {
    public boolean handled;

    public TestDomainEvent(AggregateRoot aggregateRoot) {
    }
}
