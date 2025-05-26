package core.framework.kernel.log.marker;

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
        MDC.put(CONTEXT_PRE + key, String.valueOf(value));
    }
}
