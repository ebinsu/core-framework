package core.framework.namedquery;

import java.util.List;
import java.util.Optional;

/**
 * @author ebin
 */
public interface NamedQueryService {
    <T> List<T> select(String queryName, Object... parameter);

    <T> PagingResult<T> paging(String queryName, Object... parameter);

    <T> PagingResult<T> paging(String queryName, int start, int limit, Object parameter);

    <T> Optional<T> get(String queryName, Object... parameter);
}
