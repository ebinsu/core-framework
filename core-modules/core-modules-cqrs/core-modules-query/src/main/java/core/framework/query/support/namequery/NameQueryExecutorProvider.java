package core.framework.query.support.namequery;


/**
 * @author ebin
 */
@FunctionalInterface
public interface NameQueryExecutorProvider {
    NameQueryExecutor get();
}
