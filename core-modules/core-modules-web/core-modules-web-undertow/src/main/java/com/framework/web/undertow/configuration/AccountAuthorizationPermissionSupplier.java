package com.framework.web.undertow.configuration;

import core.framework.web.common.security.filter.AuthorizationPermissionSupplier;
import io.undertow.security.api.AuthenticatedSessionManager;
import io.undertow.servlet.handlers.security.CachedAuthenticatedSessionHandler;
import io.undertow.servlet.spec.HttpSessionImpl;
import jakarta.servlet.http.HttpSession;

import java.util.Set;

/**
 * @author ebin
 */
public class AccountAuthorizationPermissionSupplier implements AuthorizationPermissionSupplier {
    @Override
    public Set<String> getPermissions(HttpSession session) {
        if (session instanceof HttpSessionImpl impl) {
            AuthenticatedSessionManager.AuthenticatedSession authenticatedSession =
                (AuthenticatedSessionManager.AuthenticatedSession) impl.getSession().getAttribute(CachedAuthenticatedSessionHandler.ATTRIBUTE_NAME);
            return authenticatedSession.getAccount().getRoles();
        }
        return Set.of();
    }
}
