package core.framework.web.security;

/**
 * @author ebin
 */
public interface CurrentUserRepository {
    String save(CurrentUser currentUser);

    CurrentUser load(String identity);

    void destroy(String identity);
}
