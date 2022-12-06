package core.framework.test.hibernate.domain;

import core.framework.jpa.hibernate.AbstractDomainEvent;

/**
 * @author ebin
 */
public class TestDomainEvent extends AbstractDomainEvent<TestDomain> {
    public boolean handled;

    public TestDomainEvent(TestDomain source) {
        super(source);
    }
}
