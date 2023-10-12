package core.framework.namequery.hibernate;

import core.framework.namequery.NamedQuery;
import core.framework.namequery.impl.AbstractNamedQueryExecutor;
import core.framework.namequery.support.parser.DefaultPlaceholderTokenHandler;
import core.framework.namequery.support.parser.TokenHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.jpa.AvailableHints;
import org.hibernate.jpa.QueryHints;
import org.hibernate.query.TupleTransformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ebin
 */
public class HibernateNamedQueryExecutor extends AbstractNamedQueryExecutor {
    private final EntityManager entityManager;
    private final Map<Class<?>, ReflectToBeanTransformer> resultBeanTransformers = new ConcurrentHashMap<>();

    public HibernateNamedQueryExecutor(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    protected <T> List<T> doExecute(String queryString, List<Pair<String, Object>> queryParameter, NamedQuery namedQuery, Integer maxReturnRows) {
        Map<String, Object> param = new HashMap<>(queryParameter.size());
        queryParameter.forEach(pair -> param.put(pair.getLeft(), pair.getRight()));
        Query query = this.createQuery(queryString, namedQuery.getResultClass(), param);
        if (maxReturnRows != null) {
            query.setHint(AvailableHints.HINT_FETCH_SIZE, maxReturnRows);
        }
        return query.getResultList();
    }

    @Override
    protected TokenHandler getTokenHandler() {
        return new DefaultPlaceholderTokenHandler();
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
