package core.framework.security.undertow;

import core.framework.json.JSON;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMechanismFactory;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.idm.PasswordCredential;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import io.undertow.util.StatusCodes;
import org.springframework.http.MediaType;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSourceChannel;

import java.nio.ByteBuffer;
import java.util.Map;

import static io.undertow.UndertowMessages.MESSAGES;

/**
 * @author ebin
 */
public class AJAXAuthenticationMechanism implements AuthenticationMechanism {
    public static final String NAME = "AJAX";
    public static final AuthenticationMechanismFactory FACTORY = new Factory();
    private final IdentityManager identityManager;

    public AJAXAuthenticationMechanism(IdentityManager identityManager) {
        this.identityManager = identityManager;
    }

    @Override
    public AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {
        int contentLength = (int) exchange.getRequestContentLength();
        String contentTypeStr = exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE);
        MediaType contentType = contentTypeStr == null ? null : MediaType.valueOf(contentTypeStr);
        if (hasBody(contentLength, exchange.getRequestMethod()) && MediaType.APPLICATION_JSON.equals(contentType)) {
            StreamSourceChannel channel = exchange.getRequestChannel();
            byte[] body = new byte[contentLength];
            try (PooledByteBuffer poolItem = exchange.getConnection().getByteBufferPool().allocate()) {
                ByteBuffer buffer = poolItem.getBuffer();
                int bytesRead;
                int position = 0;
                while (true) {
                    buffer.clear();
                    bytesRead = channel.read(buffer);
                    if (bytesRead <= 0) break;
                    buffer.flip();
                    ensureCapacity(contentLength, body, position, bytesRead);
                    buffer.get(body, position, bytesRead);
                    position += bytesRead;
                }
                if (bytesRead == -1) {
                    if (position < body.length) {
                        throw new Error(String.format("body ends prematurely, expected=%s, actual=%s", contentLength, position));
                    }
                }
                AuthenticationRequest request = JSON.fromJSON(AuthenticationRequest.class, body);
                Account account = identityManager.verify(request.username, new PasswordCredential(request.password.toCharArray()));
                if (account == null) {
                    securityContext.authenticationFailed(MESSAGES.authenticationFailed(request.username), AJAXAuthenticationMechanism.NAME);
                    return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
                } else {
                    securityContext.authenticationComplete(account, AJAXAuthenticationMechanism.NAME, true);
                    return AuthenticationMechanismOutcome.AUTHENTICATED;
                }
            } catch (Throwable e) {
                IoUtils.safeClose(channel);
            }
        }
        return AuthenticationMechanismOutcome.NOT_ATTEMPTED;
    }

    @Override
    public ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
        return new ChallengeResult(true, StatusCodes.OK);
    }

    private boolean hasBody(long contentLength, HttpString method) {
        if (contentLength == 0) return false;  // if body is empty, skip reading
        return Methods.POST.equals(method) || Methods.PUT.equals(method) || Methods.PATCH.equals(method);
    }

    private void ensureCapacity(int contentLength, byte[] body, int position, int bytesRead) {
        if (contentLength >= 0) {
            if (bytesRead + position > contentLength)
                throw new Error("body exceeds expected content length, expected=" + contentLength);
        } else {
            if (body == null) { // undertow buffer is 16k, if there is no content length, in most of cases, it's best just to create exact buffer as first read thru
                body = new byte[bytesRead];
            } else {
                int newLength = position + bytesRead;   // without content length, position will always be current length,
                byte[] bytes = new byte[newLength];     // just expend to exact read size, which is simplest way for best scenario
                System.arraycopy(body, 0, bytes, 0, position);
                body = bytes;
            }
        }
    }

    public static final class Factory implements AuthenticationMechanismFactory {

        @Deprecated
        public Factory(IdentityManager identityManager) {
        }

        public Factory() {
        }

        @Override
        public AuthenticationMechanism create(String mechanismName, IdentityManager identityManager, FormParserFactory formParserFactory, Map<String, String> properties) {
            return new AJAXAuthenticationMechanism(identityManager);
        }
    }

    public static final class AuthenticationRequest {
        public String username;
        public String password;
    }
}
