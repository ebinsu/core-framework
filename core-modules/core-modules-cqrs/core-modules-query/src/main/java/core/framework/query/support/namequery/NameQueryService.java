package core.framework.query.support.namequery;

import core.framework.query.support.PagingResult;
import core.framework.query.support.QueryParam;
import core.framework.query.support.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author ebin
 */
public class NameQueryService implements QueryService {
    private static final String TOTAL_QUERY_NAME_SUFFIX = "_total";
    private static final Logger LOGGER = LoggerFactory.getLogger(NameQueryService.class);
    protected final NameQueryRepository nameQueryRepository;
    protected final Map<QueryType, NameQueryExecutor> queryExecutors = new HashMap<>();

    public NameQueryService(NameQueryRepository nameQueryRepository) {
        this.nameQueryRepository = nameQueryRepository;
    }

    public void addQueryExecutors(NameQueryExecutor nameQueryExecutor) {
        this.queryExecutors.put(nameQueryExecutor.getQueryType(), nameQueryExecutor);
    }

    @Override
    public <T> List<T> select(QueryParam<T> queryParam) {
        NameQueryParam<T> nameQuery = nameQueryParam(queryParam);
        QueryStatement queryStatement = getQueryStatement(nameQuery.getQueryName(), nameQuery.getQueryParam());
        LOGGER.info(queryStatement.queryStatement());
        return executeSelectQuery(queryStatement, nameQuery.getResultType(), nameQuery.getQueryParam());
    }

    @Override
    public <T> PagingResult<T> select(QueryParam<T> queryParam, int start, int limit) {
        NameQueryParam<T> nameQuery = nameQueryParam(queryParam);
        QueryStatement queryStatement = getQueryStatement(nameQuery.getQueryName(), nameQuery.getQueryParam());
        LOGGER.info(queryStatement.queryStatement());
        QueryStatement totalQueryStatement = getTotalQueryStatement(nameQuery.getQueryName(), nameQuery.getQueryParam());
        LOGGER.info(totalQueryStatement.queryStatement());
        List<T> data = executePagingQuery(queryStatement, nameQuery.getResultType(), nameQuery.getQueryParam(), start, limit);
        Long total = executeTotalQuery(totalQueryStatement, nameQuery.getQueryParam());
        return new PagingResult<T>(total, data);
    }

    @Override
    public <T> Optional<T> get(QueryParam<T> queryParam) {
        NameQueryParam<T> nameQuery = nameQueryParam(queryParam);
        QueryStatement queryStatement = getQueryStatement(nameQuery.getQueryName(), nameQuery.getQueryParam());
        LOGGER.info(queryStatement.queryStatement());
        return executeGetQuery(queryStatement, nameQuery.getResultType(), nameQuery.getQueryParam());
    }

    protected Long executeTotalQuery(QueryStatement queryStatement, Map<String, Object> param) {
        NameQueryExecutor nameQueryExecutor = queryExecutors.get(queryStatement.queryType());
        if (nameQueryExecutor == null) {
            throw new RuntimeException("Query type " + queryStatement.queryType().name() + " executor not found !");
        }
        return nameQueryExecutor.executeTotalQuery(queryStatement, param);
    }

    private <T> List<T> executeSelectQuery(QueryStatement queryStatement, Class<T> beanClass, Map<String, Object> param) {
        NameQueryExecutor nameQueryExecutor = queryExecutors.get(queryStatement.queryType());
        if (nameQueryExecutor == null) {
            throw new RuntimeException("Query type " + queryStatement.queryType().name() + " executor not found !");
        }
        return nameQueryExecutor.executeSelectQuery(queryStatement, beanClass, param);
    }

    private <T> List<T> executePagingQuery(QueryStatement queryStatement, Class<T> beanClass, Map<String, Object> param, Integer start, Integer limit) {
        NameQueryExecutor nameQueryExecutor = queryExecutors.get(queryStatement.queryType());
        if (nameQueryExecutor == null) {
            throw new RuntimeException("Query type " + queryStatement.queryType().name() + " executor not found !");
        }
        return nameQueryExecutor.executePagingQuery(queryStatement, beanClass, param, start, limit);
    }

    private <T> Optional<T> executeGetQuery(QueryStatement queryStatement, Class<T> beanClass, Map<String, Object> param) {
        NameQueryExecutor nameQueryExecutor = queryExecutors.get(queryStatement.queryType());
        if (nameQueryExecutor == null) {
            throw new RuntimeException("Query type " + queryStatement.queryType().name() + " executor not found !");
        }
        return nameQueryExecutor.executeGetQuery(queryStatement, beanClass, param);
    }

    private QueryStatement getQueryStatement(String queryName, Map<String, Object> param) {
        return nameQueryRepository.getQueryStatement(queryName, param);
    }

    private QueryStatement getTotalQueryStatement(String queryName, Map<String, Object> param) {
        return getQueryStatement(queryName + TOTAL_QUERY_NAME_SUFFIX, param);
    }

    private <T> NameQueryParam<T> nameQueryParam(QueryParam<T> queryParam) {
        if (queryParam instanceof NameQueryParam) {
            return (NameQueryParam<T>) queryParam;
        } else {
            throw new UnsupportedOperationException("Only support NameQuery!");
        }
    }
}
