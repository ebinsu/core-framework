package core.framework.kernel.log;

import org.slf4j.MDC;

/**
 * @author ebin
 */
public final class Logs {
    private static final String CONTEXT_PRE = "context.";
    private static final String ACTION_KEY = "action";

    private Logs() {
    }

    public static void action(String value) {
        Logs.put(ACTION_KEY, value);
    }

    public static void put(String key, Object value) {
        Logs.doPut(CONTEXT_PRE + key, String.valueOf(value));
    }

    private static void doPut(String key, String value) {
        MDC.put(key, value);
    }
}
