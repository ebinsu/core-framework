package core.framework.security.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author ebin
 */
public final class SecurityContext {
    private static final Set<String> ANONYMOUS = new HashSet<>();
    private static final Map<String, String> PERMISSION_MAPPING = new HashMap<>();

    private SecurityContext() {
    }

    public static boolean isAnonymous(String httpMethod, String path) {
        return ANONYMOUS.contains(httpMethod + "-" + path);
    }

    public static boolean hasPermission(String httpMethod, String path, Set<String> permissions) {
        String necessaryPermission = PERMISSION_MAPPING.get(httpMethod + "-" + path);
        if (necessaryPermission != null) {
            return permissions.contains(necessaryPermission);
        }
        return false;
    }

    static void addAnonymous(String anonymousEndpoint) {
        ANONYMOUS.add(anonymousEndpoint);
    }

    static void addPermission(String endpoint, String permission) {
        PERMISSION_MAPPING.put(endpoint, permission);
    }
}
