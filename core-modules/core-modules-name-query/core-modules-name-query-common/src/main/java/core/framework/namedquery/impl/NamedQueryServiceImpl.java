package core.framework.namedquery.impl;

import core.framework.exception.marker.ErrorCodeMarker;
import core.framework.json.JSONMapper;
import core.framework.namedquery.NamedQuery;
import core.framework.namedquery.NamedQueryExecutor;
import core.framework.namedquery.NamedQueryRepository;
import core.framework.namedquery.NamedQueryService;
import core.framework.namedquery.PagingResult;
import core.framework.namedquery.configuration.NamedQueryProperties;
import core.framework.shared.utils.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author ebin
 */
public class NamedQueryServiceImpl implements NamedQueryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NamedQueryServiceImpl.class);
    private static final String TOTAL_QUERY_NAME_SUFFIX = ".total";
    private static final Integer MAX_PARAM_LENGTH = 50;
    private static final Long MAX_ELAPSED = Duration.ofSeconds(5).toNanos();
    private final NamedQueryRepository namedQueryRepository;
    private final Map<String, NamedQueryExecutor> namedQueryExecutors = new ConcurrentHashMap<>();
    private final int defaultMaxReturnSize;
    private final String startParameter;
    private final String limitParameter;

    public NamedQueryServiceImpl(NamedQueryRepository namedQueryRepository, NamedQueryProperties properties) {
        this.namedQueryRepository = namedQueryRepository;
        this.defaultMaxReturnSize = properties.getDefaultMaxReturnSize();
        this.startParameter = properties.getPagingParameter().getStart();
        this.limitParameter = properties.getPagingParameter().getLimit();
    }

    public void register(String identity, NamedQueryExecutor executor) {
        namedQueryExecutors.put(identity, executor);
    }

    @Override
    public <T> List<T> select(String queryName, Object... parameter) {
        StopWatch stopWatch = new StopWatch();
        Map<String, Object> param = getParameterMap(parameter);
        NamedQuery namedQuery = namedQueryRepository.get(queryName, param);
        NamedQueryExecutor namedQueryExecutor = namedQueryExecutors.get(namedQuery.getXmlTagName());
        if (namedQueryExecutor == null) {
            throw new RuntimeException("Query type [" + namedQuery.getQuery() + "] executor not found !");
        }
        List<T> result = namedQueryExecutor.execute(namedQuery, defaultMaxReturnSize);
        long elapsed = stopWatch.elapsed();
        track("select", queryName, param, elapsed);
        return result;
    }

    private void track(String operation, String queryName, Map<String, Object> param, long elapsed) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("select, operation={}, queryName {}, parameter {}, elapsed {}", operation, queryName, parameterString(param), elapsed);
        }
        if (elapsed >= MAX_ELAPSED) {
            LOGGER.warn(new ErrorCodeMarker("SLOW_NAMED_QUERY"), "slow named query, operation={}, queryName {}, parameter {}, elapsed {}",
                operation, queryName, parameterString(param), elapsed);
        }
    }

    @Override
    public <T> PagingResult<T> paging(String queryName, Object... parameter) {
        StopWatch stopWatch = new StopWatch();
        Map<String, Object> param = getParameterMap(parameter);

        NamedQuery namedQuery = namedQueryRepository.get(queryName, param);
        if (!namedQuery.containsQueryParameter(startParameter) || !namedQuery.containsQueryParameter(limitParameter)) {
            throw new RuntimeException("Paging query must pass the start and limit parameters !");
        }
        NamedQueryExecutor namedQueryExecutor = namedQueryExecutors.get(namedQuery.getXmlTagName());
        if (namedQueryExecutor == null) {
            throw new RuntimeException("Query type [" + namedQuery.getQuery() + "] executor not found !");
        }
        NamedQuery totalNamedQuery = namedQueryRepository.get(queryName + TOTAL_QUERY_NAME_SUFFIX, param);
        totalNamedQuery.setResultClass(Long.class);
        List<T> data = namedQueryExecutor.execute(namedQuery, Integer.parseInt(namedQuery.getQueryParameter(limitParameter).toString()));
        List<Long> totalNamedQueryResult = namedQueryExecutor.execute(totalNamedQuery, 1);
        Long total = totalNamedQueryResult.stream().findFirst().orElse(0L);
        PagingResult<T> result = new PagingResult<>(total, data);

        long elapsed = stopWatch.elapsed();
        track("paging", queryName, param, elapsed);
        return result;
    }

    @Override
    public <T> PagingResult<T> paging(String queryName, int start, int limit, Object... parameter) {
        StopWatch stopWatch = new StopWatch();
        Map<String, Object> param = getParameterMap(parameter);
        param.put(startParameter, start);
        param.put(limitParameter, limit);
        PagingResult<T> result = paging(queryName, param);

        long elapsed = stopWatch.elapsed();
        track("paging", queryName, param, elapsed);
        return result;
    }

    @Override
    public <T> Optional<T> get(String queryName, Object... parameter) {
        StopWatch stopWatch = new StopWatch();
        Map<String, Object> param = getParameterMap(parameter);

        NamedQuery namedQuery = namedQueryRepository.get(queryName, param);
        NamedQueryExecutor namedQueryExecutor = namedQueryExecutors.get(namedQuery.getXmlTagName());
        if (namedQueryExecutor == null) {
            throw new RuntimeException("Query type [" + namedQuery.getQuery() + "] executor not found !");
        }
        List<T> data = namedQueryExecutor.execute(namedQuery, 1);
        Optional<T> result = data.stream().findFirst();
        long elapsed = stopWatch.elapsed();
        track("get", queryName, param, elapsed);
        return result;
    }

    private Map<String, Object> getParameterMap(Object[] parameter) {
        Object param = null;
        if (parameter != null) {
            if (parameter.length == 1) {
                param = parameter[0];
            } else if (parameter.length > 1) {
                throw new UnsupportedOperationException("Only one parameter can be passed !");
            }
        }
        if (param == null) {
            return Map.of();
        }
        if (param instanceof Map<?, ?> map) {
            if (map.size() >= MAX_PARAM_LENGTH) {
                throw new UnsupportedOperationException("To many query parameter !");
            }
            return (Map<String, Object>) map;
        } else {
            Map<String, Object> map = JSONMapper.OBJECT_MAPPER.convertValue(param, Map.class);
            if (map.size() >= MAX_PARAM_LENGTH) {
                throw new UnsupportedOperationException("To many query parameter !");
            }
            return map;
        }
    }

    private String parameterString(Map<String, Object> parameterMap) {
        if (parameterMap == null) {
            return null;
        } else {
            return parameterMap.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining(",", "[", "]"));
        }
    }
}
