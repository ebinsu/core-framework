package core.framework.security.undertow.auth;

import core.framework.json.JSON;
import core.framework.security.common.AuthType;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMechanismFactory;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormParserFactory;

import java.util.Map;

import static io.undertow.UndertowMessages.MESSAGES;

/**
 * @author ebin
 */
public class EmailCodeAuthMechanism extends AbstractAJAXAuthMechanism {
    public static final String NAME = AuthType.EMAIL_CODE.name();
    public static final AuthenticationMechanismFactory FACTORY = new Factory();
    private final IdentityManager identityManager;

    public EmailCodeAuthMechanism(IdentityManager identityManager) {
        this.identityManager = identityManager;
    }

    @Override
    protected AuthenticationMechanismOutcome doAuthenticate(HttpServerExchange exchange, SecurityContext securityContext, byte[] requestBody) {
        //TODO valid request
        EmailCodeAuthRequest request = JSON.fromJSON(EmailCodeAuthRequest.class, requestBody);
        Account account = identityManager.verify(request.email, new CodeCredential(request.code));
        if (account == null) {
            securityContext.authenticationFailed(MESSAGES.authenticationFailed(request.email), UsernamePasswordAuthMechanism.NAME);
            return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
        } else {
            securityContext.authenticationComplete(account, UsernamePasswordAuthMechanism.NAME, true);
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
            return new EmailCodeAuthMechanism(identityManager);
        }
    }
}
