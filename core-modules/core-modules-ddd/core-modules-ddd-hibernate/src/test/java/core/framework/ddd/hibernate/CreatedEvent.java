package core.framework.ddd.hibernate;

import core.framework.ddd.common.event.AbstractDomainEvent;

/**
 * @author ebin
 */
public class CreatedEvent extends AbstractDomainEvent {
    public boolean handle = false;

    public static void main(String[] args) {
        CreatedEvent createdEvent = new CreatedEvent();
        System.out.println(createdEvent.description());
    }
}
