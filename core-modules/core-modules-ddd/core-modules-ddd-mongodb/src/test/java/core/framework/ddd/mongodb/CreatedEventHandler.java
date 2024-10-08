package core.framework.ddd.mongodb;

import core.framework.ddd.api.annotation.DomainEventHandler;

/**
 * @author ebin
 */
public class CreatedEventHandler {
    @DomainEventHandler(async = false)
    public void handle(CreatedEvent event) {
        event.handle = true;
    }
}
