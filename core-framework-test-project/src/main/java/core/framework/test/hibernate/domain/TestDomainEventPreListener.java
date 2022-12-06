package core.framework.test.hibernate.domain;

import core.framework.ddd.DomainPreEventListener;
import org.springframework.stereotype.Component;

/**
 * @author ebin
 */
@Component
public class TestDomainEventPreListener implements DomainPreEventListener<TestDomainPreEvent> {
    @Override
    public void onEvent(TestDomainPreEvent event) {
        event.handled = true;
    }
}
