package core.framework.test.hibernate.domain;

import core.framework.jpa.hibernate.AbstractDomainEvent;

/**
 * @author ebin
 */
public class AssignIdDomainEvent extends AbstractDomainEvent<AssignIdDomain> {
    public AssignIdDomainEvent(AssignIdDomain source) {
        super(source);
    }
}
