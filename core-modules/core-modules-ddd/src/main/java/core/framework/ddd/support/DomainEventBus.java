package core.framework.ddd.support;

import core.framework.ddd.AggregateRoot;
import core.framework.ddd.DomainEvent;
import core.framework.ddd.DomainEventListener;
import core.framework.ddd.DomainPostEventListener;
import core.framework.ddd.DomainPreEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ebin
 */
public final class DomainEventBus {
    public static final DomainEventBus INSTANCE = new DomainEventBus();
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainEventBus.class);
    private static final Map<String, Set<DomainPreEventListener<?>>> PRE_EVENT_LISTENERS = new ConcurrentHashMap<>();
    private static final Map<String, Set<DomainPostEventListener<?>>> POST_EVENT_LISTENERS = new ConcurrentHashMap<>();
    private ThreadPoolTaskExecutor taskExecutor;

    private DomainEventBus() {
    }

    void setTaskExecutor(ThreadPoolTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    void registerEventListener(DomainEventListener<?> listener) {
        String getGenericEventName = ((ParameterizedType) listener.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0].getTypeName();
        if (listener instanceof DomainPreEventListener) {
            PRE_EVENT_LISTENERS.computeIfAbsent(
                    getGenericEventName,
                    k -> new LinkedHashSet<>()
            ).add((DomainPreEventListener<?>) listener);
        } else if (listener instanceof DomainPostEventListener) {
            POST_EVENT_LISTENERS.computeIfAbsent(
                    getGenericEventName,
                    k -> new LinkedHashSet<>()
            ).add((DomainPostEventListener<?>) listener);
        } else {
            throw new UnsupportedOperationException("Unsupported event type!");
        }
    }

    public <T extends AggregateRoot<T>> void publishPreCommitEvent(DomainEvent<T> event) {
        Set<DomainPreEventListener<?>> domainEventListeners = PRE_EVENT_LISTENERS.get(event.getClass().getTypeName());
        if (domainEventListeners != null) {
            domainEventListeners.forEach(listener -> ((DomainPreEventListener<DomainEvent<T>>) listener).onEvent(event));
        }
    }

    public <T extends AggregateRoot<T>> void publishPostCommitEvent(DomainEvent<T> event) {
        Set<DomainPostEventListener<?>> domainEventListeners = POST_EVENT_LISTENERS.get(event.getClass().getTypeName());
        if (domainEventListeners != null) {
            domainEventListeners.forEach(listener -> {
                if (Objects.nonNull(taskExecutor) && listener.async()) {
                    taskExecutor.execute(() -> {
                        ((DomainPostEventListener<DomainEvent<T>>) listener).onEvent(event);
                    });
                } else {
                    try {
                        ((DomainPostEventListener<DomainEvent<T>>) listener).onEvent(event);
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            });
        }
    }
}
