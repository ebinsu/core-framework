package core.framework.test.hibernate.domain;


import core.framework.ddd.AbstractDomainEvent;
import core.framework.ddd.AggregateRoot;

/**
 * @author ebin
 */
public class TestDomainPreEvent extends AbstractDomainEvent {
    public boolean handled;

    public TestDomainPreEvent(AggregateRoot aggregateRoot) {
    }
}
