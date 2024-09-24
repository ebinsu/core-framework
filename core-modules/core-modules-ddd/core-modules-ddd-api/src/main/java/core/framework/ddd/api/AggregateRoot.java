package core.framework.ddd.api;

/**
 * @author ebin
 */
public interface AggregateRoot extends Entity {

    void registerEvent(DomainEvent event);
}
