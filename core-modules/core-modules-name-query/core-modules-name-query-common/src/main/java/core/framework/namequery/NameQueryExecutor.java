package core.framework.namequery;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author ebin
 */
public interface NameQueryExecutor {
    QueryType getQueryType();
    <T> List<T> executeSelectQuery(QueryStatement queryStatement, Class<T> beanClass, Map<String, Object> param);

    <T> List<T> executePagingQuery(QueryStatement queryStatement, Class<T> beanClass, Map<String, Object> param, Integer start, Integer limit);

    <T> Optional<T> executeGetQuery(QueryStatement queryStatement, Class<T> beanClass, Map<String, Object> param);

    Long executeTotalQuery(QueryStatement queryStatement, Map<String, Object> param);
}
