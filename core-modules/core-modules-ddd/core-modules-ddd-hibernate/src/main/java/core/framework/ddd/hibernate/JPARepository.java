package core.framework.ddd.hibernate;

import core.framework.ddd.api.Repository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * @author ebin
 */
public interface JPARepository<T extends AbstractAggregateRoot> extends Repository<T, BiFunction<CriteriaBuilder, Root<T>, List<Predicate>>> {
    Optional<T> find(String queryString, Object... params);

    List<T> select(String queryString, Object... params);
}
