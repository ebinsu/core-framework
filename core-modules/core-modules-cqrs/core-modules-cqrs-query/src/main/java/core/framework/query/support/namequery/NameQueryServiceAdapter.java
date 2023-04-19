package core.framework.query.support.namequery;

import core.framework.query.support.NameQueryParam;
import core.framework.query.support.NameQueryService;
import core.framework.query.support.PagingResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

/**
 * @author ebin
 */
public class NameQueryServiceAdapter implements core.framework.query.support.NameQueryService {
    @Autowired
    private NameQueryService nameQueryService;

    @Override
    public <T> List<T> select(NameQueryParam<T> queryParam) {
        return nameQueryService.select(queryParam);
    }

    @Override
    public <T> PagingResult<T> select(NameQueryParam<T> queryParam, int start, int limit) {
        return nameQueryService.select(queryParam, start, limit);
    }

    @Override
    public <T> Optional<T> get(NameQueryParam<T> queryParam) {
        return nameQueryService.get(queryParam);
    }
}
