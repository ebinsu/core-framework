package core.framework.query.hibernate;

import core.framework.query.QueryParser;
import core.framework.query.impl.AbstractQueryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.hibernate.jpa.AvailableHints;
import org.hibernate.jpa.QueryHints;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ebin
 */
public class HibernateMysqlQueryService extends AbstractQueryService {
    private final EntityManager entityManager;
    private final Map<Class<?>, AliasToJSONBeanTransformer> resultBeanTransformers = new ConcurrentHashMap<>();

    public HibernateMysqlQueryService(EntityManager entityManager, QueryParser queryParser) {
        super(queryParser);
        this.entityManager = entityManager;
    }

    @Override
    protected String getQueryStatement(String queryName, Map<String, Object> param) {
        return this.queryParser.getQueryString(queryName, param);
    }

    @Override
    protected String getTotalQueryStatement(String queryName, Map<String, Object> param) {
        String sql = getQueryStatement(queryName, param);
        return this.queryParser.parseTotalQueryString(sql);
    }

    protected <T> List<T> executeSelectQuery(String sql, Class<T> beanClass, Map<String, Object> param) {
        Query query = this.createQuery(sql, beanClass, param);
        return query.getResultList();
    }

    protected <T> List<T> executePagingQuery(String sql, Class<T> beanClass, Map<String, Object> param, Integer start, Integer limit) {
        Query query = this.createQuery(sql, beanClass, param);
        query.setFirstResult(start).setMaxResults(limit).setHint(QueryHints.HINT_FETCH_SIZE, limit);
        return query.getResultList();
    }

    protected <T> Optional<T> executeGetQuery(String sql, Class<T> beanClass, Map<String, Object> param) {
        Query query = this.createQuery(sql, beanClass, param);
        query.setHint(AvailableHints.HINT_FETCH_SIZE, 1);
        return query.getResultList().stream().findFirst();
    }

    @Override
    protected Long executeTotalQuery(String sql, Map<String, Object> param) {
        TotalQueryResult total = executeGetQuery(sql, TotalQueryResult.class, param).orElse(TotalQueryResult.EMPTY);
        return total.getTotal().longValue();
    }

    private <T> Query createQuery(String sql, Class<T> beanType, Map<String, Object> param) {
        Query query = entityManager.createNativeQuery(sql).setHint(QueryHints.HINT_READONLY, true);
        param.forEach(query::setParameter);
        org.hibernate.query.Query<?> unwrapQuery = query.unwrap(org.hibernate.query.Query.class);
        unwrapQuery.setTupleTransformer(getTransformer(beanType));
        return query;
    }

    private <T> AliasToJSONBeanTransformer getTransformer(Class<T> beanType) {
        return resultBeanTransformers.computeIfAbsent(beanType, k -> new AliasToJSONBeanTransformer(beanType));
    }
}
