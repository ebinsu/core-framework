package core.framework.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author ebin
 */
public class PermissionMapping {
    public static Set<String> anonymous = new HashSet<>();
    public static Map<String, Set<String>> permissions = new HashMap<>();
}
