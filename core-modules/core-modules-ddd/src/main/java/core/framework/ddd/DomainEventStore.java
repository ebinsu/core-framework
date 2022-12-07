package core.framework.ddd;

/**
 * @author ebin
 */
public interface DomainEventStore {
    void persist(AggregateRoot<?> aggregateRoot);
}
