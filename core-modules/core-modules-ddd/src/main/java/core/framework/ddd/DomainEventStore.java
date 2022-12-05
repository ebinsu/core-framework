package core.framework.ddd;

/**
 * @author ebin
 */
public interface DomainEventStore {
    <T extends AggregateRoot<T>> void persist(DomainEvent<T> event);
}
