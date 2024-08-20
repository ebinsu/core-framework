package com.framework.web.undertow.security.authentication.mailecode;

import com.framework.web.undertow.configuration.AuthenticationCustomizer;
import com.framework.web.undertow.security.authentication.AbstractAJAXAuthMechanism;
import com.framework.web.undertow.security.authentication.RequestValidFailedException;
import core.framework.json.JSON;
import core.framework.web.common.security.AuthenticationType;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMechanismFactory;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormParserFactory;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.util.StringUtils;

import java.util.Map;

import static io.undertow.UndertowMessages.MESSAGES;

/**
 * @author ebin
 */
public class EmailCodeAuthMechanism extends AbstractAJAXAuthMechanism {
    public static final String NAME = AuthenticationType.EMAIL_CODE.name();
    public static final AuthenticationMechanismFactory FACTORY = new Factory();
    private final IdentityManager identityManager;

    public EmailCodeAuthMechanism(IdentityManager identityManager, String method, String uri) {
        super(method, uri);
        this.identityManager = identityManager;
    }

    @Override
    protected AuthenticationMechanismOutcome doAuthenticate(HttpServerExchange exchange, SecurityContext securityContext, byte[] requestBody) {
        EmailCodeAuthRequest request = JSON.fromJSON(EmailCodeAuthRequest.class, requestBody);
        if (!(EmailValidator.getInstance().isValid(request.email) && StringUtils.hasLength(request.code) && request.code.length() == 6)) {
            throw new RequestValidFailedException();
        }
        Account account = identityManager.verify(request.email, new StringCredential(request.code));
        if (account == null) {
            securityContext.authenticationFailed(MESSAGES.authenticationFailed(request.email), EmailCodeAuthMechanism.NAME);
            return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
        } else {
            securityContext.authenticationComplete(account, EmailCodeAuthMechanism.NAME, true);
            return AuthenticationMechanismOutcome.AUTHENTICATED;
        }
    }

    public static class EmailCodeAuthRequest {
        public String email;
        public String code;
    }

    public static final class Factory implements AuthenticationMechanismFactory {
        @Override
        public AuthenticationMechanism create(String mechanismName, IdentityManager identityManager, FormParserFactory formParserFactory, Map<String, String> properties) {
            return new EmailCodeAuthMechanism(identityManager, properties.get(AuthenticationCustomizer.AUTHENTICATION_METHOD), properties.get(AuthenticationCustomizer.AUTHENTICATION_URL));
        }
    }
}
