package core.framework.namequery.impl;

import core.framework.namequery.NamedQuery;
import core.framework.namequery.NamedQueryExecutor;
import core.framework.namequery.NamedQueryRepository;
import core.framework.namequery.NamedQueryService;
import core.framework.namequery.PagingResult;
import core.framework.namequery.QueryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author ebin
 */
public class NamedQueryServiceImpl implements NamedQueryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NamedQueryService.class);
    private static final int DEFAULT_RETURN_ROWS = 64;
    private static final String START = "start";
    private static final String LIMIT = "limit";
    private static final String TOTAL_QUERY_NAME_SUFFIX = ".total";
    private NamedQueryRepository namedQueryRepository;
    private Map<QueryType, NamedQueryExecutor> namedQueryExecutors;

    public NamedQueryServiceImpl(NamedQueryRepository namedQueryRepository,
                                 Map<QueryType, NamedQueryExecutor> namedQueryExecutors) {
        this.namedQueryRepository = namedQueryRepository;
        this.namedQueryExecutors = namedQueryExecutors;
    }

    @Override
    public <T> List<T> select(String queryName, Object parameter) {
        NamedQuery namedQuery = namedQueryRepository.get(queryName, parameter);
        NamedQueryExecutor namedQueryExecutor = namedQueryExecutors.get(namedQuery.getQueryType());
        if (namedQueryExecutor == null) {
            throw new RuntimeException("Query type [" + namedQuery.getQuery() + "] executor not found !");
        }
        return namedQueryExecutor.execute(namedQuery, DEFAULT_RETURN_ROWS);
    }

    @Override
    public <T> PagingResult<T> paging(String queryName, Object parameter) {
        NamedQuery namedQuery = namedQueryRepository.get(queryName, parameter);
        if (!namedQuery.getQueryParameter().containsKey(START) || !namedQuery.getQueryParameter().containsKey(LIMIT)) {
            throw new RuntimeException("Paging query must pass the start and limit parameters !");
        }
        NamedQueryExecutor namedQueryExecutor = namedQueryExecutors.get(namedQuery.getQueryType());
        if (namedQueryExecutor == null) {
            throw new RuntimeException("Query type [" + namedQuery.getQuery() + "] executor not found !");
        }
        NamedQuery totalNamedQuery = namedQueryRepository.get(queryName + TOTAL_QUERY_NAME_SUFFIX, parameter);
        List<T> data = namedQueryExecutor.execute(namedQuery, Integer.parseInt(namedQuery.getQueryParameter().get(START).toString()));
        List<Long> totalNamedQueryResult = namedQueryExecutor.execute(totalNamedQuery, 1);
        Long total = totalNamedQueryResult.stream().findFirst().orElse(0L);
        return new PagingResult<>(total, data);
    }

    @Override
    public <T> Optional<T> get(String queryName, Object parameter) {
        NamedQuery namedQuery = namedQueryRepository.get(queryName, parameter);
        if (!namedQuery.getQueryParameter().containsKey(START) || !namedQuery.getQueryParameter().containsKey(LIMIT)) {
            throw new RuntimeException("Paging query must pass the start and limit parameters !");
        }
        NamedQueryExecutor namedQueryExecutor = namedQueryExecutors.get(namedQuery.getQueryType());
        if (namedQueryExecutor == null) {
            throw new RuntimeException("Query type [" + namedQuery.getQuery() + "] executor not found !");
        }
        List<T> data = namedQueryExecutor.execute(namedQuery, 1);
        return data.stream().findFirst();
    }
}
