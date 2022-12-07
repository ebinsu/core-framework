package core.framework.query.support;

import core.framework.query.Query;
import core.framework.query.QueryBus;
import core.framework.query.QueryHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ebin
 */
public class QueryBusImpl implements QueryBus {
    private final Map<Class<? extends Query<?>>, QueryHandler<?, ?>> queryHandlers = new HashMap<>();

    @Override
    public <T> T dispatch(Query<T> query) {
        QueryHandler<Query<T>, T> queryHandler = (QueryHandler<Query<T>, T>) queryHandlers.get(query.getClass());
        if (queryHandler == null) {
            throw new RuntimeException("Query handler not found!");
        }
        return queryHandler.handle(query);
    }

    @Override
    public void register(QueryHandler<? extends Query<?>, ?> handler) {
        Type genericSuperclass = handler.getClass().getGenericSuperclass();
        Type[] interfaces;
        if (genericSuperclass instanceof Class<?>
                && QueryHandler.class.isAssignableFrom((Class<?>) genericSuperclass)) {
            interfaces = ((Class<?>) genericSuperclass).getGenericInterfaces();
        } else {
            interfaces = handler.getClass().getGenericInterfaces();
        }
        ParameterizedType handlerInterface = Arrays.stream(interfaces).filter(type -> QueryHandler.class == ((ParameterizedType) type).getRawType())
                .findFirst().map(m -> (ParameterizedType) m)
                .orElseThrow();
        Type[] actualTypeArguments = handlerInterface.getActualTypeArguments();
        if (actualTypeArguments.length > 0) {
            queryHandlers.put((Class<? extends Query<?>>) handlerInterface.getActualTypeArguments()[0], handler);
        }
    }
}
