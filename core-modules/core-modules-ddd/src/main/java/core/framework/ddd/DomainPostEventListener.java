package core.framework.ddd;

/**
 * @author ebin
 */
public interface DomainPostEventListener<T extends DomainEvent<? extends AggregateRoot<?>>> extends DomainEventListener<T> {
    default boolean async() {
        return true;
    }
}
