package core.framework.ddd.hibernate;

import core.framework.ddd.api.annotation.DomainEventHandler;

/**
 * @author ebin
 */
public class CreatedEventHandler {
    @DomainEventHandler
    public void handle(CreatedEvent event) {
        event.handle = true;
    }
}
