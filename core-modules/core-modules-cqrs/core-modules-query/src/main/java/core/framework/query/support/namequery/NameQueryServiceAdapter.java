package core.framework.query.support.namequery;

import core.framework.query.support.PagingResult;
import core.framework.query.support.QueryParam;
import core.framework.query.support.QueryService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

/**
 * @author ebin
 */
public class NameQueryServiceAdapter implements QueryService {
    @Autowired
    private NameQueryService nameQueryService;

    @Override
    public <T> List<T> select(QueryParam<T> queryParam) {
        return nameQueryService.select(queryParam);
    }

    @Override
    public <T> PagingResult<T> select(QueryParam<T> queryParam, int start, int limit) {
        return nameQueryService.select(queryParam, start, limit);
    }

    @Override
    public <T> Optional<T> get(QueryParam<T> queryParam) {
        return nameQueryService.get(queryParam);
    }
}
