package core.framework.ddd.hibernate.internal.event;

import core.framework.ddd.api.DomainEvent;
import core.framework.ddd.api.DomainEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * @author ebin
 */
public final class DomainEventBusImpl implements DomainEventBus {
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainEventBusImpl.class);

    private final Executor taskExecutor;
    private final Map<String, List<InvocableDomainEventHandlerMethod>> domainEventHandlerMethods = new ConcurrentHashMap<>();

    public DomainEventBusImpl(Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Override
    public void dispatch(DomainEvent domainEvent) {
        List<InvocableDomainEventHandlerMethod> invocableDomainEventHandlerMethods;
        invocableDomainEventHandlerMethods = domainEventHandlerMethods.get(domainEvent.getClass().getTypeName());

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

    void subscribe(InvocableDomainEventHandlerMethod invocableQueryHandlerMethod) {
        domainEventHandlerMethods.computeIfAbsent(
            invocableQueryHandlerMethod.getEventName(),
            k -> new ArrayList<>()
        ).add(invocableQueryHandlerMethod);
    }
}
