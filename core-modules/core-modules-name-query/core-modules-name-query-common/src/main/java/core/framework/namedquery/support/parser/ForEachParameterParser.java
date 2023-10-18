package core.framework.namedquery.support.parser;

import org.apache.commons.lang3.tuple.Pair;

/**
 * @author ebin
 */
public final class ForEachParameterParser {

    public static final String COLLECTION_PROPERTIES_PREFIX = "__collection__";
    public static final String COLLECTION_INDEX_SEPARATOR = "__";
    public static final String COLLECTION_EXPRESSION_TEMPLATE = COLLECTION_PROPERTIES_PREFIX + "%1$s" + COLLECTION_INDEX_SEPARATOR + "%2$d";

    private ForEachParameterParser() {
    }

    public static String get(String collection, int index) {
        return String.format(COLLECTION_EXPRESSION_TEMPLATE, collection, index);
    }

    public static Pair<String, Integer> parse(String parameterName) {
        if (parameterName.startsWith(COLLECTION_PROPERTIES_PREFIX)) {
            String[] split = parameterName.replace(COLLECTION_PROPERTIES_PREFIX, "").split(COLLECTION_INDEX_SEPARATOR);
            return Pair.of(split[0], Integer.parseInt(split[1]));
        } else {
            return null;
        }
    }
}
