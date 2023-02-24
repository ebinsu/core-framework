package core.framework.ddd;

import java.util.List;

/**
 * @author ebin
 */
public interface AggregateRoot<T extends AggregateRoot<T, ID>, ID> extends Entity<T, ID> {
    DomainEvent<T> registerEvent(DomainEvent<T> event);

    List<DomainEvent<T>> getDomainEvents();

    void clearDomainEvents();
}
