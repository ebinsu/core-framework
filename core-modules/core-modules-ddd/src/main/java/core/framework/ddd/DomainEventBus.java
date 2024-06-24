package core.framework.ddd;

import core.framework.ddd.annotation.Trigger;
import core.framework.ddd.support.InvocableDomainEventHandlerMethod;

/**
 * @author ebin
 */
public interface DomainEventBus {
    void dispatch(DomainEvent domainEvent, Trigger trigger);

    void subscribe(InvocableDomainEventHandlerMethod invocableQueryHandlerMethod);
}
