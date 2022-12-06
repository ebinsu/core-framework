package core.framework.test.hibernate.domain;

import core.framework.jpa.hibernate.AbstractDomainEvent;

/**
 * @author ebin
 */
public class TestDomainPreEvent extends AbstractDomainEvent<TestDomain> {
    public boolean handled;

    public TestDomainPreEvent(TestDomain source) {
        super(source);
    }
}
