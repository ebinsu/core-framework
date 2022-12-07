package core.framework.query.support;

import java.util.List;
import java.util.Optional;

/**
 * @author ebin
 */
public interface QueryService {
    <T> List<T> select(QueryParam<T> queryParam);

    <T> PagingResult<T> select(QueryParam<T> queryParam, int start, int limit);

    <T> Optional<T> get(QueryParam<T> queryParam);
}
