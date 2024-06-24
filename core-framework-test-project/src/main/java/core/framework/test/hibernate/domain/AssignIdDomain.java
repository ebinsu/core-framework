package core.framework.test.hibernate.domain;

import core.framework.ddd.annotation.AggregateRoot;
import core.framework.jpa.common.AbstractAggregateRoot;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Random;

/**
 * @author ebin
 */
@AggregateRoot
@Table(name = "AssignIdDomain")
public class AssignIdDomain extends AbstractAggregateRoot {

    @Id
    public Long abc;

    public String name;

    @Override
    public Long getId() {
        return abc;
    }

    private AssignIdDomain() {
    }

    public AssignIdDomain(Long abc) {
        this.abc = abc;
    }
}
