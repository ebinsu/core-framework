package core.framework.ddd;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author ebin
 */
public interface AggregateRoot<T extends AggregateRoot<T, ID>, ID> extends Entity<T, ID> {
    ZonedDateTime getCreatedTime();

    DomainEvent<T> registerEvent(DomainEvent<T> event);

    List<DomainEvent<T>> getDomainEvents();

    void clearDomainEvents();
}
