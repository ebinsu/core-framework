package core.framework.ddd.api;

/**
 * @author ebin
 */
public interface DomainEventBus {

    void dispatch(DomainEvent domainEvent);
}
