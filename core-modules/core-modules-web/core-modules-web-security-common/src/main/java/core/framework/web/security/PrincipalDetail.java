package core.framework.web.security;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author ebin
 */
public class PrincipalDetail {
    private final String id;
    private final String name;
    private final String displayName;
    private final Set<String> permissionCodes;

    private final String clientIP;
    private final Map<String, Object> attributes;

    public PrincipalDetail(String id, String name, String displayName, List<String> permissionCodes, String clientIP) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.permissionCodes = Optional.ofNullable(permissionCodes).map(HashSet::new).orElse(new HashSet<>());
        this.clientIP = clientIP;
        this.attributes = new HashMap<>();
    }


    public PrincipalDetail(String id, String name, String displayName, Set<String> permissionCodes, String clientIP) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.permissionCodes = permissionCodes;
        this.clientIP = clientIP;
        this.attributes = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Set<String> getPermissionCodes() {
        return Collections.unmodifiableSet(permissionCodes);
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public String getClientIP() {
        return clientIP;
    }

    public Object getAttribute(String attrName) {
        return attributes.get(attrName);
    }

    public void addAttribute(String attrName, Object attr) {
        attributes.put(attrName, attrName);
    }

    public boolean hasPermission(Set<String> requirePermissions) {
        if (requirePermissions == null) {
            return true;
        } else {
            return requirePermissions.stream().anyMatch(getPermissionCodes()::contains);
        }
    }
}
