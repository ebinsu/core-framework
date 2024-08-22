package core.framework.shared.log;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author ebin
 */
public final class LogAttribute {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAttribute.class);
    private static final ThreadLocal<Map<String, String>> ATTRIBUTE_MAP = ThreadLocal.withInitial(HashMap::new);
    private static final String NULL_STRING = "null";
    private static final String KEYWORD = "=";

    private LogAttribute() {
    }

    public static void info(String key, Object... values) {
        Map<String, String> stringStringMap = ATTRIBUTE_MAP.get();
        String value = stringStringMap.get(key);
        if (StringUtils.isEmpty(value)) {
            value = valuesString(values);
        } else {
            value = value + " , " + valuesString(values);
        }
        if (value.contains(KEYWORD)) {
            value = value.replace(KEYWORD, "*");
        }
        stringStringMap.put(key, value);
        LOGGER.info(new LogAttributeMarker(), "{} = {}", key, value);
    }

    public static String get(String key) {
        Map<String, String> stringStringMap = ATTRIBUTE_MAP.get();
        return stringStringMap.get(key);
    }

    public static void end() {
        ATTRIBUTE_MAP.remove();
    }

    private static String valuesString(Object... values) {
        if (values == null) {
            return NULL_STRING;
        } else if (values.length == 1) {
            if (values[0] == null) {
                return NULL_STRING;
            } else {
                return String.valueOf(values[0]);
            }
        } else {
            return Stream.of(values).map(m -> {
                if (m == null) {
                    return NULL_STRING;
                } else {
                    return String.valueOf(m);
                }
            }).collect(Collectors.joining(",", "[", "]"));
        }
    }
}
