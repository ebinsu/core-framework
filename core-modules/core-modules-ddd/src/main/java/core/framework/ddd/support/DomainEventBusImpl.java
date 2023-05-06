package core.framework.ddd.support;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.DomainEvent;
import core.framework.ddd.DomainEventBus;
import core.framework.ddd.annotation.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author ebin
 */
public final class DomainEventBusImpl implements DomainEventBus {
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainEventBusImpl.class);

    private final ThreadPoolTaskExecutor taskExecutor;
    private final Map<String, List<InvocableDomainEventHandlerMethod>> beforeCommitDomainEventHandlerMethods = new HashMap<>();
    private final Map<String, List<InvocableDomainEventHandlerMethod>> afterCommitDomainEventHandlerMethods = new HashMap<>();

    public DomainEventBusImpl(ThreadPoolTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Override
    public <T extends AggregateRoot<T, ?>> void dispatch(DomainEvent<T, ?> domainEvent, Trigger trigger) {
        List<InvocableDomainEventHandlerMethod> invocableDomainEventHandlerMethods;
        if (Trigger.BEFORE_COMMIT == trigger) {
            invocableDomainEventHandlerMethods = beforeCommitDomainEventHandlerMethods.get(domainEvent.getClass().getTypeName());
        } else if (Trigger.AFTER_COMMIT == trigger) {
            invocableDomainEventHandlerMethods = afterCommitDomainEventHandlerMethods.get(domainEvent.getClass().getTypeName());
        } else {
            throw new UnsupportedOperationException("Unsupported trigger type!");
        }
        if (invocableDomainEventHandlerMethods != null) {
            invocableDomainEventHandlerMethods.forEach(methods -> {
                if (Objects.nonNull(taskExecutor) && methods.isAsync()) {
                    taskExecutor.execute(() -> {
                        try {
                            methods.invoke(domainEvent);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    try {
                        methods.invoke(domainEvent);
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            });
        }
    }

    @Override
    public void subscribe(InvocableDomainEventHandlerMethod invocableQueryHandlerMethod) {
        if (Trigger.BEFORE_COMMIT == invocableQueryHandlerMethod.getTrigger()) {
            beforeCommitDomainEventHandlerMethods.computeIfAbsent(
                    invocableQueryHandlerMethod.getEventName(),
                    k -> new ArrayList<>()
            ).add(invocableQueryHandlerMethod);
        } else if (Trigger.AFTER_COMMIT == invocableQueryHandlerMethod.getTrigger()) {
            afterCommitDomainEventHandlerMethods.computeIfAbsent(
                    invocableQueryHandlerMethod.getEventName(),
                    k -> new ArrayList<>()
            ).add(invocableQueryHandlerMethod);
        } else {
            throw new UnsupportedOperationException("Unsupported trigger type!");
        }
    }
}
