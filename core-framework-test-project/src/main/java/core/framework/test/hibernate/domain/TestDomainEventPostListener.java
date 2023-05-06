package core.framework.test.hibernate.domain;

import core.framework.ddd.annotation.DomainEventHandler;
import org.springframework.stereotype.Component;

/**
 * @author ebin
 */
@Component
public class TestDomainEventPostListener {
    @DomainEventHandler
    public void onEvent(TestDomainEvent event) {
        event.handled = true;
    }
}
