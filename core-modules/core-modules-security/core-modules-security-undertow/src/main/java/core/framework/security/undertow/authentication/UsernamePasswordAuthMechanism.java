package core.framework.security.undertow.authentication;

import core.framework.json.JSON;
import core.framework.security.common.AuthenticationType;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMechanismFactory;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.idm.PasswordCredential;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormParserFactory;

import java.util.Map;

import static io.undertow.UndertowMessages.MESSAGES;

/**
 * @author ebin
 */
public class UsernamePasswordAuthMechanism extends AbstractAJAXAuthMechanism {
    public static final String NAME = AuthenticationType.USERNAME_PASSWORD.name();
    public static final AuthenticationMechanismFactory FACTORY = new Factory();
    private final IdentityManager identityManager;

    public UsernamePasswordAuthMechanism(IdentityManager identityManager, String method, String uri) {
        super(method, uri);
        this.identityManager = identityManager;
    }

    @Override
    protected AuthenticationMechanismOutcome doAuthenticate(HttpServerExchange exchange, SecurityContext securityContext, byte[] requestBody) {
        AuthenticationRequest request = JSON.fromJSON(AuthenticationRequest.class, requestBody);
        if (request.username == null || request.password == null) {
            throw new RequestValidFailedException();
        }
        Account account = identityManager.verify(request.username, new PasswordCredential(request.password.toCharArray()));
        if (account == null) {
            securityContext.authenticationFailed(MESSAGES.authenticationFailed(request.username), UsernamePasswordAuthMechanism.NAME);
            return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
        } else {
            securityContext.authenticationComplete(account, UsernamePasswordAuthMechanism.NAME, true);
            return AuthenticationMechanismOutcome.AUTHENTICATED;
        }
    }

    public static final class Factory implements AuthenticationMechanismFactory {
        @Override
        public AuthenticationMechanism create(String mechanismName, IdentityManager identityManager, FormParserFactory formParserFactory, Map<String, String> properties) {
            return new UsernamePasswordAuthMechanism(identityManager, properties.get(AuthenticationCustomizer.AUTHENTICATION_METHOD), properties.get(AuthenticationCustomizer.AUTHENTICATION_URL));
        }
    }

    public static final class AuthenticationRequest {
        public String username;
        public String password;
    }
}
