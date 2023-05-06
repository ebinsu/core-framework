package core.framework.test.hibernate.domain;

import core.framework.ddd.annotation.DomainEventHandler;
import core.framework.ddd.annotation.Trigger;
import org.springframework.stereotype.Component;

/**
 * @author ebin
 */
@Component
public class TestDomainEventPreListener {

    @DomainEventHandler(trigger = Trigger.BEFORE_COMMIT, async = false)
    public void onEvent(TestDomainPreEvent event) {
        event.handled = true;
    }
}
