package core.framework.ddd.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.jpa.HibernateHints;
import org.hibernate.query.named.NamedObjectRepository;
import org.hibernate.query.sql.spi.NamedNativeQueryMemento;
import org.hibernate.query.sqm.spi.NamedSqmQueryMemento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

/**
 * @author ebin
 */
public abstract class AbstractJPARepository<T extends AbstractAggregateRoot> implements JPARepository<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractJPARepository.class);
    private static final int START_INDEX = 0;
    private static final int HINT_FETCH_SIZE = 1;

    private final Class<T> entityClass;

    public AbstractJPARepository() {
        Type actualTypeArgument = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.entityClass = (Class<T>) actualTypeArgument;
    }

    @Override
    public void persist(T entity) {
        if (entity != null) {
            getEntityManager().persist(entity);
        }
    }

    @Override
    public T merge(T entity) {
        return getEntityManager().merge(entity);
    }

    @Override
    public void remove(T entity) {
        if (entity != null) {
            getEntityManager().remove(entity);
        }
    }

    @Override
    public Optional<T> find(Serializable id) {
        return (id != null) ? Optional.ofNullable(getEntityManager().find(getEntityClass(), id)) : Optional.empty();
    }

    @Override
    public Optional<T> find(BiFunction<CriteriaBuilder, Root<T>, List<Predicate>> predicateSupplier) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<T> root = criteriaQuery.from(entityClass);

        predicateSupplier.apply(criteriaBuilder, root).forEach(criteriaQuery::where);

        TypedQuery<T> query = getEntityManager().createQuery(criteriaQuery);
        query.setHint(HibernateHints.HINT_FETCH_SIZE, HINT_FETCH_SIZE);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public Optional<T> find(String queryString, Object... params) {
        NamedObjectRepository namedObjectRepository = getNamedObjectRepository();
        final NamedSqmQueryMemento namedSqmQueryMemento = namedObjectRepository.getSqmQueryMemento(queryString);
        if (namedSqmQueryMemento != null) {
            // name query
            return Optional.ofNullable(findByNamedQuery(queryString, params));
        }
        final NamedNativeQueryMemento namedNativeDescriptor = namedObjectRepository.getNativeQueryMemento(queryString);
        if (namedNativeDescriptor != null) {
            // native query
            return Optional.ofNullable(findByNamedQuery(queryString, params));
        }
        // sql
        return Optional.ofNullable(findByNativeQuery(queryString, params));
    }

    @Override
    public List<T> select(BiFunction<CriteriaBuilder, Root<T>, List<Predicate>> predicateSupplier) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<T> root = criteriaQuery.from(entityClass);

        predicateSupplier.apply(criteriaBuilder, root).forEach(criteriaQuery::where);

        TypedQuery<T> query = getEntityManager().createQuery(criteriaQuery);
        return query.getResultList();
    }

    @Override
    public List<T> select(String queryString, Object... params) {
        NamedObjectRepository namedObjectRepository = getNamedObjectRepository();
        final NamedSqmQueryMemento namedSqmQueryMemento = namedObjectRepository.getSqmQueryMemento(queryString);
        if (namedSqmQueryMemento != null) {
            // name query
            return selectByNamedQuery(queryString, params);
        }
        final NamedNativeQueryMemento namedNativeDescriptor = namedObjectRepository.getNativeQueryMemento(queryString);
        if (namedNativeDescriptor != null) {
            // native query
            return selectByNamedQuery(queryString, params);
        }
        // sql
        return selectByNativeQuery(queryString, params);
    }

    protected <R> R aggregateSingle(String queryString, Class<R> resultClass, Object... params) {
        NamedObjectRepository namedObjectRepository = getNamedObjectRepository();
        final NamedSqmQueryMemento namedSqmQueryMemento = namedObjectRepository.getSqmQueryMemento(queryString);
        List<R> results;
        if (namedSqmQueryMemento != null) {
            // name query
            results = aggregateByNamedQuery(queryString, resultClass, params);
        } else {
            final NamedNativeQueryMemento namedNativeDescriptor = namedObjectRepository.getNativeQueryMemento(queryString);
            if (namedNativeDescriptor != null) {
                // native query result class only support entity.
                results = aggregateByNativeQuery(namedNativeDescriptor.getSqlString(), resultClass, params);
            } else {
                // sql
                results = aggregateByNativeQuery(queryString, resultClass, params);
            }
        }
        if (results.size() != 1) {
            LOGGER.warn("The aggregate single query :{} fetch more than one result.", queryString);
        }
        return results.getFirst();
    }

    protected <R> List<R> aggregate(String queryString, Class<R> resultClass, Object... params) {
        NamedObjectRepository namedObjectRepository = getNamedObjectRepository();
        final NamedSqmQueryMemento namedSqmQueryMemento = namedObjectRepository.getSqmQueryMemento(queryString);
        if (namedSqmQueryMemento != null) {
            // name query
            return aggregateByNamedQuery(queryString, resultClass, params);
        }
        final NamedNativeQueryMemento namedNativeDescriptor = namedObjectRepository.getNativeQueryMemento(queryString);
        if (namedNativeDescriptor != null) {
            // native query result class only support entity.
            return aggregateByNativeQuery(namedNativeDescriptor.getSqlString(), resultClass, params);
        }
        // sql
        return aggregateByNativeQuery(queryString, resultClass, params);
    }

    protected abstract EntityManager getEntityManager();

    protected Class<T> getEntityClass() {
        return this.entityClass;
    }

    private NamedObjectRepository getNamedObjectRepository() {
        SessionFactoryImplementor sessionFactory = getEntityManager().getEntityManagerFactory()
            .unwrap(SessionFactoryImplementor.class);
        return sessionFactory.getQueryEngine().getNamedObjectRepository();
    }

    private T findByNamedQuery(String queryName, Object... params) {
        TypedQuery<T> namedQuery = getEntityManager().createNamedQuery(queryName, getEntityClass());
        if (params != null) {
            IntStream.range(START_INDEX, params.length).forEach(index -> namedQuery.setParameter(index + 1, params[index]));
        }
        namedQuery.setHint(HibernateHints.HINT_FETCH_SIZE, HINT_FETCH_SIZE);
        List<T> resultList = namedQuery.getResultList();
        return resultList.stream().findFirst().orElse(null);
    }

    private T findByNativeQuery(String sql, Object... params) {
        Query nativeQuery = getEntityManager().createNativeQuery(sql, getEntityClass());
        if (params != null) {
            IntStream.range(START_INDEX, params.length).forEach(index -> nativeQuery.setParameter(index + 1, params[index]));
        }
        nativeQuery.setHint(HibernateHints.HINT_FETCH_SIZE, HINT_FETCH_SIZE);
        List<T> resultList = nativeQuery.getResultList();
        return resultList.stream().findFirst().orElse(null);
    }

    private List<T> selectByNativeQuery(String sql, Object... params) {
        Query nativeQuery = getEntityManager().createNativeQuery(sql, getEntityClass());
        if (params != null) {
            IntStream.range(START_INDEX, params.length).forEach(index -> nativeQuery.setParameter(index + 1, params[index]));
        }
        List<T> resultList = nativeQuery.getResultList();
        if (resultList == null)
            return List.of();
        return resultList;
    }

    private List<T> selectByNamedQuery(String queryName, Object... params) {
        TypedQuery<T> namedQuery = getEntityManager().createNamedQuery(queryName, getEntityClass());
        if (params != null) {
            IntStream.range(START_INDEX, params.length).forEach(index -> namedQuery.setParameter(index + 1, params[index]));
        }
        List<T> resultList = namedQuery.getResultList();
        if (resultList == null)
            return List.of();
        return resultList;
    }

    private <R> List<R> aggregateByNamedQuery(String queryName, Class<R> resultClass, Object... params) {
        TypedQuery<R> namedQuery = getEntityManager().createNamedQuery(queryName, resultClass);
        if (params != null) {
            IntStream.range(START_INDEX, params.length).forEach(index -> namedQuery.setParameter(index + 1, params[index]));
        }
        return namedQuery.getResultList();
    }

    private <R> List<R> aggregateByNativeQuery(String sql, Class<R> resultClass, Object... params) {
        Query nativeQuery = getEntityManager().createNativeQuery(sql, resultClass);
        if (params != null) {
            IntStream.range(START_INDEX, params.length).forEach(index -> nativeQuery.setParameter(index + 1, params[index]));
        }
        return nativeQuery.getResultList();
    }
}
