package core.framework.query;

/**
 * @author ebin
 */
public interface QueryBus {
    <T> T dispatch(Object query);
}
