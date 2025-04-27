package core.framework.web.security.session;

import core.framework.web.security.AuthStrategy;
import core.framework.web.security.PrincipalDetail;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;
import java.util.Set;

/**
 * @author ebin
 */
public class SessionAuthStrategy implements AuthStrategy {
    private static final String PERMISSION_CODES = "PERMISSION_CODES";
    private static final String NAME = "NAME";
    private static final String DISPLAY_NAME = "DISPLAY_NAME";
    private static final String CLIENT_IP = "CLIENT_IP";
    private static final String ID = "ID";

    @Override
    public String authenticate(HttpServletRequest request, HttpServletResponse response, PrincipalDetail principalDetail) {
        HttpSession session = request.getSession();
        session.setAttribute(ID, principalDetail.getId());
        session.setAttribute(NAME, principalDetail.getName());
        session.setAttribute(DISPLAY_NAME, principalDetail.getDisplayName());
        session.setAttribute(PERMISSION_CODES, principalDetail.getPermissionCodes());
        session.setAttribute(CLIENT_IP, principalDetail.getClientIP());
        return session.getId();
    }

    @Override
    public PrincipalDetail load(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        } else {
            return new PrincipalDetail(
                (String) session.getAttribute(ID),
                (String) session.getAttribute(NAME),
                (String) session.getAttribute(DISPLAY_NAME),
                (Set<String>) session.getAttribute(PERMISSION_CODES),
                (String) session.getAttribute(CLIENT_IP)
            );
        }
    }

    @Override
    public void destroy(HttpServletRequest request, HttpServletResponse response) {
        Optional.ofNullable(request.getSession(false)).ifPresent(HttpSession::invalidate);
    }
}
