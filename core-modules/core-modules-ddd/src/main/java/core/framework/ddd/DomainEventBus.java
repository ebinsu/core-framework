package core.framework.ddd;

import core.framework.ddd.annotation.Trigger;
import core.framework.ddd.support.InvocableDomainEventHandlerMethod;

/**
 * @author ebin
 */
public interface DomainEventBus {
    <T extends AggregateRoot<T, ?>> void dispatch(DomainEvent<T, ?> domainEvent, Trigger trigger);

    void subscribe(InvocableDomainEventHandlerMethod invocableQueryHandlerMethod);
}
