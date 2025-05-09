package core.framework.web.security;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ebin
 */
public abstract class AbstractCurrentUserRepository implements CurrentUserRepository {
    protected static final String ATTRIBUTES = "ATTRIBUTES";
    protected static final String PERMISSION_CODES = "PERMISSION_CODES";
    protected static final String NAME = "NAME";
    protected static final String DISPLAY_NAME = "DISPLAY_NAME";
    protected static final String ID = "ID";

    private final CurrentUserVerifyCustomize currentUserVerifyCustomize;

    public AbstractCurrentUserRepository(CurrentUserVerifyCustomize currentUserVerifyCustomize) {
        this.currentUserVerifyCustomize = currentUserVerifyCustomize;
    }

    @Override
    public String save(CurrentUser currentUser) {
        Map<String, Object> currentUserMap = new HashMap<>(5);
        currentUserMap.put(ATTRIBUTES, currentUser.getAttributes());
        currentUserMap.put(PERMISSION_CODES, currentUser.getPermissionCodes());
        currentUserMap.put(NAME, currentUser.getName());
        currentUserMap.put(DISPLAY_NAME, currentUser.getDisplayName());
        currentUserMap.put(ID, currentUser.getId());
        return doSave(currentUser, currentUserMap);
    }

    @Override
    public CurrentUser load(String identity) {
        if (StringUtils.isEmpty(identity)) {
            return null;
        }
        Map<String, Object> map = doLoad(identity);
        if (map == null) {
            return null;
        }
        CurrentUser currentUser = new CurrentUser(
            (String) map.get(ID),
            (String) map.get(NAME),
            (String) map.get(DISPLAY_NAME),
            (Collection<String>) map.get(PERMISSION_CODES)
        );
        Map<String, String> attributes = (Map<String, String>) map.get(ATTRIBUTES);
        currentUser.putAttributes(attributes);
        if (currentUserVerifyCustomize != null && !currentUserVerifyCustomize.verify(currentUser)) {
            return null;
        }
        return currentUser;
    }

    protected abstract String doSave(CurrentUser currentUser, Map<String, Object> currentUserMap);

    protected abstract Map<String, Object> doLoad(String identity);
}
