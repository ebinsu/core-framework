package core.framework.jpa.hibernate;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.Entity;
import jakarta.persistence.MappedSuperclass;

/**
 * @author ebin
 */
@MappedSuperclass
public abstract class AbstractEntity<A extends AggregateRoot<A, ?>, ID> implements Entity<A, ID> {

}
