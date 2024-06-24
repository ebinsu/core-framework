package core.framework.ddd;

import java.util.List;

/**
 * @author ebin
 */
public interface AggregateRoot extends Entity {
    void registerEvent(DomainEvent event);

    List<DomainEvent> getDomainEvents();

    void clearDomainEvents();
}
