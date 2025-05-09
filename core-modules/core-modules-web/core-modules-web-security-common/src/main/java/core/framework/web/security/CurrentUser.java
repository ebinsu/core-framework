package core.framework.web.security;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author ebin
 */
public class CurrentUser {
    private final String id;
    private final String name;
    private final String displayName;
    private final Set<String> permissionCodes;

    private final Map<String, String> attributes;

    public CurrentUser(String id, String name, String displayName, Collection<String> permissionCodes) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.permissionCodes = Optional.ofNullable(permissionCodes).map(HashSet::new).orElse(new HashSet<>());
        this.attributes = new HashMap<>();
    }

    public CurrentUser(String id, String name, String displayName, Set<String> permissionCodes) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.permissionCodes = permissionCodes;
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

    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public String getAttribute(String attrName) {
        return attributes.get(attrName);
    }

    public void addAttribute(String attrName, String attr) {
        attributes.put(attrName, attr);
    }

    public boolean hasPermission(Set<String> requirePermissions) {
        if (requirePermissions == null) {
            return true;
        } else {
            return requirePermissions.stream().anyMatch(getPermissionCodes()::contains);
        }
    }

    public void putAttributes(Map<String, String> attributes) {
        this.attributes.putAll(attributes);
    }
}
