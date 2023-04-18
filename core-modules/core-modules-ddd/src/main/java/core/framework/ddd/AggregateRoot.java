package core.framework.ddd;

import java.util.List;

/**
 * @author ebin
 */
public interface AggregateRoot<T extends AggregateRoot<T, ID>, ID> extends Entity<T, ID> {
    DomainEvent<T, ID> registerEvent(DomainEvent<T, ID> event);

    List<DomainEvent<T, ID>> getDomainEvents();

    void clearDomainEvents();
}
