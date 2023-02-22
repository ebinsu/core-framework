package core.framework.ddd;

/**
 * @author ebin
 */
public interface DomainEventListener<T extends DomainEvent<? extends AggregateRoot<?, ?>>> {
    void onEvent(T event);
}
