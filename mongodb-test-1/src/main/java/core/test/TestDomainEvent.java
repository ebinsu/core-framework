package core.test;


import core.framework.jpa.common.AbstractDomainEvent;

/**
 * @author ebin
 */
public class TestDomainEvent extends AbstractDomainEvent<TestDomain, String> {
    public boolean handled;

    public TestDomainEvent(TestDomain source) {
        super(source);
    }
}
