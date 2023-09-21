package core.framework.test.hibernate.domain;


import core.framework.ddd.support.AbstractDomainEvent;

/**
 * @author ebin
 */
public class TestDomainEvent extends AbstractDomainEvent<TestDomain, Long> {
    public boolean handled;

}
