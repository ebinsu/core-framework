package core.framework.query.support;

/**
 * @author ebin
 */
public interface NameQueryParam<T> {
    String getQueryName();

    Class<T> getResultType();
}
