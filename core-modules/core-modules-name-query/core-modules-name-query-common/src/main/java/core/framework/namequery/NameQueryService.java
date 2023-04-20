package core.framework.namequery;

import java.util.List;
import java.util.Optional;

/**
 * @author ebin
 */
public interface NameQueryService {
    <T> List<T> select(NameQueryParam<T> queryParam);

    <T> PagingResult<T> select(NameQueryParam<T> queryParam, int start, int limit);

    <T> Optional<T> get(NameQueryParam<T> queryParam);
}
