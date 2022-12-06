package core.framework.test.hibernate.domain;

import core.framework.ddd.DomainPostEventListener;
import org.springframework.stereotype.Component;

/**
 * @author ebin
 */
@Component
public class TestDomainEventPostListener implements DomainPostEventListener<TestDomainEvent> {
    @Override
    public void onEvent(TestDomainEvent event) {
        event.handled = true;
    }
}
