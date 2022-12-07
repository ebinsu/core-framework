package core.framework.query;

/**
 * @author ebin
 */
public interface QueryBus {
    <T> T dispatch(Query<T> query);

    void register(QueryHandler<? extends Query<?>, ?> handler);
}
