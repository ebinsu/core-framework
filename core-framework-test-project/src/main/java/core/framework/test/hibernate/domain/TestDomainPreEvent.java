package core.framework.test.hibernate.domain;


import core.framework.ddd.support.AbstractDomainEvent;

/**
 * @author ebin
 */
public class TestDomainPreEvent extends AbstractDomainEvent<TestDomain, Long> {
    public boolean handled;
}
