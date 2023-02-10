package core.framework.security.authorization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author ebin
 */
public class SecurityContext {
    private static final Set<String> anonymous = new HashSet<>();
    private static final Map<String, String> permissionMapping = new HashMap<>();

    public boolean isAnonymous(String endpoint) {
        return anonymous.contains(endpoint);
    }

    public boolean hasPermission(String endpoint, Set<String> permissions) {
        String necessaryPermission = permissionMapping.get(endpoint);
        if (necessaryPermission != null) {
            return permissions.contains(necessaryPermission);
        }
        return false;
    }

    protected static void addAnonymous(String anonymousEndpoint) {
        anonymous.add(anonymousEndpoint);
    }

    protected static void addPermission(String endpoint, String permission) {
        permissionMapping.put(endpoint, permission);
    }
}
