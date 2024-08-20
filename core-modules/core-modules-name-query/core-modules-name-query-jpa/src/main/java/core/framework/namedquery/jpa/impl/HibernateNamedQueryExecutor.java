package core.framework.namedquery.jpa.impl;

import core.framework.namedquery.NamedQuery;
import core.framework.namedquery.NamedQueryExecutor;
import core.framework.namedquery.support.parser.GenericTokenParser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.jpa.AvailableHints;
import org.hibernate.jpa.QueryHints;
import org.hibernate.query.TupleTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ebin
 */
public class HibernateNamedQueryExecutor implements NamedQueryExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateNamedQueryExecutor.class);
    private final EntityManager entityManager;
    private final Map<Class<?>, ReflectToBeanTransformer> resultBeanTransformers = new ConcurrentHashMap<>();
    private final int batchSize;

    public HibernateNamedQueryExecutor(EntityManager entityManager, int batchSize) {
        this.entityManager = entityManager;
        this.batchSize = batchSize;
    }

    @Override
    public <T> List<T> execute(NamedQuery namedQuery, Integer maxReturnRows) {
        String queryString = namedQuery.getQuery();
        NamedPlaceholderTokenHandler handler = new NamedPlaceholderTokenHandler();
        GenericTokenParser parser = new GenericTokenParser("#{", "}", handler);
        queryString = parser.parse(queryString);
        List<Pair<String, Object>> queryParameter = handler.getParameterNames().stream().map(paramName ->
            Pair.of(paramName, namedQuery.getQueryParameter(paramName))
        ).toList();
        Map<String, Object> param = new HashMap<>(queryParameter.size());
        queryParameter.forEach(pair -> param.put(pair.getLeft(), pair.getRight()));
        if (param.isEmpty()) {
            param.putAll(namedQuery.getQueryParameters());
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Named query: [{}], query string: [{}], Query parameter: [{}]", namedQuery.getName(), queryString, param);
        }
        Query query = this.createQuery(queryString, namedQuery.getResultClass(), param);
        if (maxReturnRows != null) {
            query.setMaxResults(maxReturnRows);
            query.setHint(AvailableHints.HINT_FETCH_SIZE, Math.min(batchSize, maxReturnRows));
        } else {
            query.setHint(AvailableHints.HINT_FETCH_SIZE, batchSize);
        }
        return query.getResultList();
    }

    private <T> Query createQuery(String sql, Class<T> beanType, Map<String, Object> param) {
        Query query = entityManager.createNativeQuery(sql).setHint(QueryHints.HINT_READONLY, true);
        query.getParameters().forEach(parameter ->
            Optional.ofNullable(param.get(parameter.getName())).ifPresent(value -> query.setParameter(parameter.getName(), value))
        );
        org.hibernate.query.Query<?> unwrapQuery = query.unwrap(org.hibernate.query.Query.class);
        unwrapQuery.setTupleTransformer(getTransformer(beanType));
        return query;
    }

    private <T> TupleTransformer<?> getTransformer(Class<T> beanType) {
        return resultBeanTransformers.computeIfAbsent(beanType, k -> new ReflectToBeanTransformer(beanType));
    }
}
