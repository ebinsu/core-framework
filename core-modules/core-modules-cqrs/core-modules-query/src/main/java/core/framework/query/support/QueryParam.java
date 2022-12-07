package core.framework.query.support;

/**
 * @author ebin
 */
public interface QueryParam<T> {
    String getQueryName();

    Class<T> getResultType();
}
