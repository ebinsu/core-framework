package core.framework.ddd;

/**
 * @author ebin
 */
public interface DomainPreEventListener<T extends DomainEvent<? extends AggregateRoot<?, ?>, ?>> extends DomainEventListener<T> {
}
