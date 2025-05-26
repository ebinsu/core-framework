package core.framework.ddd.mongodb;

import core.framework.ddd.common.event.AbstractDomainEvent;

/**
 * @author ebin
 */
public class CreatedEvent extends AbstractDomainEvent {
    public boolean handle = false;
}
