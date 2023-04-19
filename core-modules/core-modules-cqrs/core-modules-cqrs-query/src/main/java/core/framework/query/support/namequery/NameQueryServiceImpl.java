package core.framework.query.support.namequery;

import core.framework.query.support.NameQueryService;
import core.framework.query.support.PagingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author ebin
 */
public class NameQueryServiceImpl implements NameQueryService {
    private static final String TOTAL_QUERY_NAME_SUFFIX = "_total";
    private static final Logger LOGGER = LoggerFactory.getLogger(NameQueryServiceImpl.class);
    protected final NameQueryRepository nameQueryRepository;
    protected final Map<QueryType, NameQueryExecutor> queryExecutors = new HashMap<>();

    public NameQueryServiceImpl(NameQueryRepository nameQueryRepository) {
        this.nameQueryRepository = nameQueryRepository;
    }

    public void addQueryExecutors(NameQueryExecutor nameQueryExecutor) {
        this.queryExecutors.put(nameQueryExecutor.getQueryType(), nameQueryExecutor);
    }

    @Override
    public <T> List<T> select(core.framework.query.support.NameQueryParam<T> queryParam) {
        NameQueryParamImpl<T> nameQuery = nameQueryParam(queryParam);
        QueryStatement queryStatement = getQueryStatement(nameQuery.getQueryName(), nameQuery.getQueryParam());
        LOGGER.info(queryStatement.queryStatement());
        return executeSelectQuery(queryStatement, nameQuery.getResultType(), nameQuery.getQueryParam());
    }

    @Override
    public <T> PagingResult<T> select(core.framework.query.support.NameQueryParam<T> queryParam, int start, int limit) {
        NameQueryParamImpl<T> nameQuery = nameQueryParam(queryParam);
        QueryStatement queryStatement = getQueryStatement(nameQuery.getQueryName(), nameQuery.getQueryParam());
        LOGGER.info(queryStatement.queryStatement());
        QueryStatement totalQueryStatement = getTotalQueryStatement(nameQuery.getQueryName(), nameQuery.getQueryParam());
        LOGGER.info(totalQueryStatement.queryStatement());
        List<T> data = executePagingQuery(queryStatement, nameQuery.getResultType(), nameQuery.getQueryParam(), start, limit);
        Long total = executeTotalQuery(totalQueryStatement, nameQuery.getQueryParam());
        return new PagingResult<T>(total, data);
    }

    @Override
    public <T> Optional<T> get(core.framework.query.support.NameQueryParam<T> queryParam) {
        NameQueryParamImpl<T> nameQuery = nameQueryParam(queryParam);
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

    private <T> NameQueryParamImpl<T> nameQueryParam(core.framework.query.support.NameQueryParam<T> queryParam) {
        if (queryParam instanceof NameQueryParamImpl) {
            return (NameQueryParamImpl<T>) queryParam;
        } else {
            throw new UnsupportedOperationException("Only support NameQuery!");
        }
    }
}
