package core.framework.namedquery;

import java.util.Map;

/**
 * @author ebin
 */
public interface NamedQuery {
    String getName();

    String getQuery();

    Map<String, Object> getQueryParameters();

    Object getQueryParameter(String parameterName);

    boolean containsQueryParameter(String parameterName);

    QueryType getQueryType();

    Class<?> getResultClass();
}
