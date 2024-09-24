package core.framework.ddd.api;

/**
 * @author ebin
 */
public interface DomainEventStorage {
    void persist(AggregateRoot aggregateRoot);
}
