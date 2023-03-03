package core.framework.security.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author ebin
 */
public class SecurityContext {
    private static final Set<String> ANONYMOUS = new HashSet<>();
    private static final Map<String, String> PERMISSION_MAPPING = new HashMap<>();

    public boolean isAnonymous(String endpoint) {
        return ANONYMOUS.contains(endpoint);
    }

    public boolean hasPermission(String endpoint, Set<String> permissions) {
        String necessaryPermission = PERMISSION_MAPPING.get(endpoint);
        if (necessaryPermission != null) {
            return permissions.contains(necessaryPermission);
        }
        return false;
    }

    protected static void addAnonymous(String anonymousEndpoint) {
        ANONYMOUS.add(anonymousEndpoint);
    }

    protected static void addPermission(String endpoint, String permission) {
        PERMISSION_MAPPING.put(endpoint, permission);
    }
}
