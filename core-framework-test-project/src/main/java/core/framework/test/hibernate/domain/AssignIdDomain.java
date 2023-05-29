package core.framework.test.hibernate.domain;

import core.framework.ddd.annotation.AggregateRoot;
import core.framework.jpa.common.AbstractAggregateRoot;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author ebin
 */
@AggregateRoot
@Table(name = "AssignIdDomain")
public class AssignIdDomain extends AbstractAggregateRoot<AssignIdDomain, Long> {

    @Id
    public Long abc;

    public String name;

    @Override
    public Long getId() {
        return abc;
    }
}
