package core.framework.namequery.support.parser;

import java.util.List;
import java.util.Locale;

/**
 * @author ebin
 */
public class TrimHelper {
    private final String prefix;
    private final String suffix;
    private final List<String> prefixesToOverride;
    private final List<String> suffixesToOverride;
    private boolean prefixApplied;
    private boolean suffixApplied;
    private StringBuilder queryBuffer;

    public TrimHelper(String query,
                      String prefix, List<String> prefixesToOverride,
                      String suffix, List<String> suffixesToOverride) {
        this.prefixApplied = false;
        this.suffixApplied = false;
        this.queryBuffer = new StringBuilder(query.trim());
        this.prefix = prefix;
        this.prefixesToOverride = prefixesToOverride;
        this.suffix = suffix;
        this.suffixesToOverride = suffixesToOverride;
    }

    public String trim(boolean forceApply) {
        String trimmedUppercaseSql = queryBuffer.toString().toUpperCase(Locale.ENGLISH);
        if (forceApply) {
            applyPrefix(queryBuffer, trimmedUppercaseSql);
            applySuffix(queryBuffer, trimmedUppercaseSql);
        } else {
            if (trimmedUppercaseSql.length() > 0) {
                applyPrefix(queryBuffer, trimmedUppercaseSql);
                applySuffix(queryBuffer, trimmedUppercaseSql);
            }
        }
        return queryBuffer.toString();
    }

    private void applyPrefix(StringBuilder sql, String trimmedUppercaseSql) {
        if (!prefixApplied) {
            prefixApplied = true;
            if (prefixesToOverride != null) {
                for (String toRemove : prefixesToOverride) {
                    if (trimmedUppercaseSql.startsWith(toRemove)) {
                        sql.delete(0, toRemove.trim().length());
                        break;
                    }
                }
            }
            if (prefix != null) {
                sql.insert(0, " ");
                sql.insert(0, prefix);
            }
        }
    }

    private void applySuffix(StringBuilder sql, String trimmedUppercaseSql) {
        if (!suffixApplied) {
            suffixApplied = true;
            if (suffixesToOverride != null) {
                for (String toRemove : suffixesToOverride) {
                    if (trimmedUppercaseSql.endsWith(toRemove) || trimmedUppercaseSql.endsWith(toRemove.trim())) {
                        int start = sql.length() - toRemove.trim().length();
                        int end = sql.length();
                        sql.delete(start, end);
                        break;
                    }
                }
            }
            if (suffix != null) {
                sql.append(" ");
                sql.append(suffix);
            }
        }
    }
}
