package core.framework.namequery;


/**
 * @author ebin
 */
@FunctionalInterface
public interface NameQueryExecutorProvider {
    NameQueryExecutor get();
}
