package core.framework.test.hibernate.domain;


import core.framework.jpa.common.AbstractDomainEvent;

/**
 * @author ebin
 */
public class TestDomainPreEvent extends AbstractDomainEvent<TestDomain, Long> {
    public boolean handled;

    public TestDomainPreEvent(TestDomain source) {
        super(source);
    }
}
