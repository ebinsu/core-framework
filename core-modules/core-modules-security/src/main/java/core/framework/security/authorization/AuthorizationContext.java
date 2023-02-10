package core.framework.security.authorization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author ebin
 */
public class AuthorizationContext {
    private static final Set<String> anonymous = new HashSet<>();
    private static final Map<String, Set<String>> permissionMapping = new HashMap<>();

    public boolean isAnonymous(String endpoint) {
        return anonymous.contains(endpoint);
    }

    protected static void addAnonymous(String anonymousEndpoint) {
        anonymous.add(anonymousEndpoint);
    }

    protected static void addPermissions(String endpoint, Set<String> permissions) {
        permissionMapping.put(endpoint, permissions);
    }
}
