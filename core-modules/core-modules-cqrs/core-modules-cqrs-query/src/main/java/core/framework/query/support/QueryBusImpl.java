package core.framework.query.support;

import core.framework.query.QueryBus;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ebin
 */
public class QueryBusImpl implements QueryBus {
    private final Map<Class<?>, InvocableQueryHandlerMethod> queryHandlerMethods = new HashMap<>();

    @Override
    public <T> T dispatch(Object query) {
        Class<?> queryClass = query.getClass();
        InvocableQueryHandlerMethod invocableQueryHandlerMethod = queryHandlerMethods.get(queryClass);
        if (invocableQueryHandlerMethod == null) {
            throw new RuntimeException("Query handle not found, Query is :" + queryClass.getName());
        }
        try {
            return (T) invocableQueryHandlerMethod.invoke(query);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void subscribe(InvocableQueryHandlerMethod invocableQueryHandlerMethod) {
        synchronized (this) {
            queryHandlerMethods.put(invocableQueryHandlerMethod.getQueryClass(), invocableQueryHandlerMethod);
        }
    }
}
