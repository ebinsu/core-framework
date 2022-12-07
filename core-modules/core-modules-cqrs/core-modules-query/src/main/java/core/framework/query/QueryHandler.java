package core.framework.query;

/**
 * @author ebin
 */
public interface QueryHandler<Q extends Query<R>, R> {
    R handle(Q query);
}
