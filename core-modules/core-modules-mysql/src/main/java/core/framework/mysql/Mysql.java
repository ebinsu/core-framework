package core.framework.mysql;

/**
 * @author ebin
 */
public interface Mysql {
    ThreadLocal<Boolean> SUPPRESS_SLOW_SQL = ThreadLocal.withInitial(() -> Boolean.FALSE);

    static void suppressSlowSQLWarning(boolean suppress) {
        SUPPRESS_SLOW_SQL.set(suppress);
    }
}
