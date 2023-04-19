package core.framework.query.support;

import core.framework.query.QueryBus;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ebin
 */
public class QueryBusImpl implements QueryBus {
    private final Map<String, InvocableQueryHandlerMethod> queryHandlerMethods = new HashMap<>();

    @Override
    public <T> T dispatch(Object query) {
        String name = query.getClass().getName();
        InvocableQueryHandlerMethod invocableQueryHandlerMethod = queryHandlerMethods.get(name);
        if (invocableQueryHandlerMethod == null) {
            throw new RuntimeException("Query handle not found, Query name :" + name);
        }
        try {
            return (T) invocableQueryHandlerMethod.invoke(query);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribe(InvocableQueryHandlerMethod invocableQueryHandlerMethod) {
        synchronized (this) {
            queryHandlerMethods.put(invocableQueryHandlerMethod.getQueryName(), invocableQueryHandlerMethod);
        }
    }
}
