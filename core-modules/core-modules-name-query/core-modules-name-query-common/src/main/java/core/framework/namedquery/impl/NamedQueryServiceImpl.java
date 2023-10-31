package core.framework.namedquery.impl;

import core.framework.json.JSONMapper;
import core.framework.namedquery.NamedQuery;
import core.framework.namedquery.NamedQueryExecutor;
import core.framework.namedquery.NamedQueryRepository;
import core.framework.namedquery.NamedQueryService;
import core.framework.namedquery.PagingResult;
import core.framework.namedquery.QueryType;
import core.framework.namedquery.configuration.NamedQueryProperties;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ebin
 */
public class NamedQueryServiceImpl implements NamedQueryService {
    private static final String TOTAL_QUERY_NAME_SUFFIX = ".total";
    private final NamedQueryRepository namedQueryRepository;
    private final Map<QueryType, NamedQueryExecutor> namedQueryExecutors = new ConcurrentHashMap<>();
    private final int defaultMaxReturnSize;
    private final String startParameter;
    private final String limitParameter;

    public NamedQueryServiceImpl(NamedQueryRepository namedQueryRepository, NamedQueryProperties properties) {
        this.namedQueryRepository = namedQueryRepository;
        this.defaultMaxReturnSize = properties.getDefaultMaxReturnSize();
        this.startParameter = properties.getPagingParameter().getStart();
        this.limitParameter = properties.getPagingParameter().getLimit();
    }

    public void register(QueryType type, NamedQueryExecutor executor) {
        namedQueryExecutors.put(type, executor);
    }

    @Override
    public <T> List<T> select(String queryName, Object... parameter) {
        Map<String, Object> param = getParam(parameter);
        NamedQuery namedQuery = namedQueryRepository.get(queryName, param);
        NamedQueryExecutor namedQueryExecutor = namedQueryExecutors.get(namedQuery.getQueryType());
        if (namedQueryExecutor == null) {
            throw new RuntimeException("Query type [" + namedQuery.getQuery() + "] executor not found !");
        }
        return namedQueryExecutor.execute(namedQuery, defaultMaxReturnSize);
    }


    @Override
    public <T> PagingResult<T> paging(String queryName, Object... parameter) {
        Map<String, Object> param = getParam(parameter);

        NamedQuery namedQuery = namedQueryRepository.get(queryName, param);
        if (!namedQuery.containsQueryParameter(startParameter) || !namedQuery.containsQueryParameter(limitParameter)) {
            throw new RuntimeException("Paging query must pass the start and limit parameters !");
        }
        NamedQueryExecutor namedQueryExecutor = namedQueryExecutors.get(namedQuery.getQueryType());
        if (namedQueryExecutor == null) {
            throw new RuntimeException("Query type [" + namedQuery.getQuery() + "] executor not found !");
        }
        NamedQuery totalNamedQuery = namedQueryRepository.get(queryName + TOTAL_QUERY_NAME_SUFFIX, param);
        List<T> data = namedQueryExecutor.execute(namedQuery, Integer.parseInt(namedQuery.getQueryParameter(limitParameter).toString()));
        List<Long> totalNamedQueryResult = namedQueryExecutor.execute(totalNamedQuery, 1);
        Long total = totalNamedQueryResult.stream().findFirst().orElse(0L);
        return new PagingResult<>(total, data);
    }

    @Override
    public <T> PagingResult<T> paging(String queryName, int start, int limit, Object... parameter) {
        Map<String, Object> param = getParam(parameter);
        param.put(startParameter, start);
        param.put(limitParameter, limit);
        return paging(queryName, param);
    }

    @Override
    public <T> Optional<T> get(String queryName, Object... parameter) {
        Map<String, Object> param = getParam(parameter);

        NamedQuery namedQuery = namedQueryRepository.get(queryName, param);
        NamedQueryExecutor namedQueryExecutor = namedQueryExecutors.get(namedQuery.getQueryType());
        if (namedQueryExecutor == null) {
            throw new RuntimeException("Query type [" + namedQuery.getQuery() + "] executor not found !");
        }
        List<T> data = namedQueryExecutor.execute(namedQuery, 1);
        return data.stream().findFirst();
    }

    private static Map<String, Object> getParam(Object[] parameter) {
        Object param = null;
        if (parameter != null) {
            if (parameter.length == 1) {
                param = parameter[0];
            } else if (parameter.length > 1) {
                throw new UnsupportedOperationException("Only one parameter can be passed !");
            }
        }
        if (param instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        } else {
            return (Map<String, Object>) JSONMapper.OBJECT_MAPPER.convertValue(parameter, Map.class);
        }
    }
}
