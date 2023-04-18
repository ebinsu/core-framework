package core.framework.test.hibernate.domain;

import core.framework.jpa.hibernate.AbstractDomainEvent;

/**
 * @author ebin
 */
public class AssignIdDomainEvent extends AbstractDomainEvent<AssignIdDomain, Long> {
    public AssignIdDomainEvent(AssignIdDomain source) {
        super(source);
    }
}
