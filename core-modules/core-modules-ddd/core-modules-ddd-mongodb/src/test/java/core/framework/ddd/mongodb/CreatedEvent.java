package core.framework.ddd.mongodb;

import core.framework.ddd.api.AbstractDomainEvent;

/**
 * @author ebin
 */
public class CreatedEvent extends AbstractDomainEvent {
    public boolean handle = false;
}
