package core.framework.ddd;

import java.time.ZonedDateTime;

/**
 * @author ebin
 */
public interface DomainEvent<T extends AggregateRoot<T, ID>, ID> {
    ID getAggregateRootId();

    Class<T> getAggregateRootClass();

    ZonedDateTime getCreatedTime();
}

