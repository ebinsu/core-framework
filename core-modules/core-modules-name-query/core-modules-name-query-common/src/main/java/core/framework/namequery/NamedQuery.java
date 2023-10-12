package core.framework.namequery;

import java.util.Map;

/**
 * @author ebin
 */
public interface NamedQuery {
    String getQuery();

    Map<String, Object> getQueryParameter();

    QueryType getQueryType();

    Class<?> getResultClass();
}
