package core.framework.namequery.hibernate;


import core.framework.namequery.NameQueryExecutor;
import core.framework.namequery.QueryStatement;
import core.framework.namequery.QueryType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.hibernate.jpa.AvailableHints;
import org.hibernate.jpa.QueryHints;
import org.hibernate.query.TupleTransformer;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ebin
 */
public class HibernateNameQueryExecutor implements NameQueryExecutor {
    private final EntityManager entityManager;
    private final Map<Class<?>, ReflectToBeanTransformer> resultBeanTransformers = new ConcurrentHashMap<>();

    public HibernateNameQueryExecutor(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public QueryType getQueryType() {
        return QueryType.SQL;
    }

    @Override
    public <T> List<T> executeSelectQuery(QueryStatement queryStatement, Class<T> beanClass, Map<String, Object> param) {
        Query query = this.createQuery(queryStatement.queryStatement(), beanClass, param);
        return query.getResultList();
    }

    @Override
    public <T> List<T> executePagingQuery(QueryStatement queryStatement, Class<T> beanClass, Map<String, Object> param, Integer start, Integer limit) {
        Query query = this.createQuery(queryStatement.queryStatement(), beanClass, param);
        query.setFirstResult(start).setMaxResults(limit).setHint(QueryHints.HINT_FETCH_SIZE, limit);
        return query.getResultList();
    }

    @Override
    public <T> Optional<T> executeGetQuery(QueryStatement queryStatement, Class<T> beanClass, Map<String, Object> param) {
        Query query = this.createQuery(queryStatement.queryStatement(), beanClass, param);
        query.setHint(AvailableHints.HINT_FETCH_SIZE, 1);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public Long executeTotalQuery(QueryStatement queryStatement, Map<String, Object> param) {
        TotalQueryResult total = executeGetQuery(queryStatement, TotalQueryResult.class, param).orElse(TotalQueryResult.EMPTY);
        return total.getTotal().longValue();
    }

    private <T> Query createQuery(String sql, Class<T> beanType, Map<String, Object> param) {
        Query query = entityManager.createNativeQuery(sql).setHint(QueryHints.HINT_READONLY, true);
        param.forEach(query::setParameter);
        org.hibernate.query.Query<?> unwrapQuery = query.unwrap(org.hibernate.query.Query.class);
        unwrapQuery.setTupleTransformer(getTransformer(beanType));
        return query;
    }

    private <T> TupleTransformer<?> getTransformer(Class<T> beanType) {
        return resultBeanTransformers.computeIfAbsent(beanType, k -> new ReflectToBeanTransformer(beanType));
    }

}
