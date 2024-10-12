package core.framework.ddd.hibernate;

import core.framework.ddd.api.Repository;
import jakarta.persistence.Query;

import java.util.List;
import java.util.Optional;

/**
 * @author ebin
 */
public interface JPARepository<T extends AbstractAggregateRoot> extends Repository<T, Query> {
    Optional<T> find(String queryString, Object... params);

    List<T> select(String queryString, Object... params);
}
