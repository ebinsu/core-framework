package core.framework.query;

import core.framework.query.support.InvocableQueryHandlerMethod;
import jakarta.annotation.Nonnull;

/**
 * @author ebin
 */
public interface QueryBus {
    <T> T dispatch(Object query);

    void subscribe(@Nonnull InvocableQueryHandlerMethod invocableQueryHandlerMethod);
}
