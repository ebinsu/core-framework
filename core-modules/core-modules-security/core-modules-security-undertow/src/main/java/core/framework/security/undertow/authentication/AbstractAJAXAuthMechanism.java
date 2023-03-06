package core.framework.security.undertow.authentication;

import io.undertow.connector.PooledByteBuffer;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import io.undertow.util.StatusCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSourceChannel;

import java.nio.ByteBuffer;

/**
 * @author ebin
 */
public abstract class AbstractAJAXAuthMechanism implements AuthenticationMechanism {
    private final Logger logger = LoggerFactory.getLogger(AbstractAJAXAuthMechanism.class);

    private final String method;
    private final String uri;

    public AbstractAJAXAuthMechanism(String method, String uri) {
        this.method = method;
        this.uri = uri;
    }

    @Override
    public AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {
        if (!(exchange.getRequestMethod().equalToString(method) && exchange.getRequestPath().equals(uri))) {
            return AuthenticationMechanismOutcome.NOT_ATTEMPTED;
        }
        int contentLength = (int) exchange.getRequestContentLength();
        String contentTypeStr = exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE);
        MediaType contentType = contentTypeStr == null ? null : MediaType.valueOf(contentTypeStr);
        if (hasBody(contentLength, exchange.getRequestMethod()) && MediaType.APPLICATION_JSON.equals(contentType)) {
            StreamSourceChannel channel = exchange.getRequestChannel();
            if (channel == null) {
                return AuthenticationMechanismOutcome.NOT_ATTEMPTED;
            }
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
                    body = ensureCapacity(contentLength, body, position, bytesRead);
                    buffer.get(body, position, bytesRead);
                    position += bytesRead;
                }
                if (bytesRead == -1 && position < body.length) {
                    throw new Error(String.format("body ends prematurely, expected=%s, actual=%s", contentLength, position));
                }
                return doAuthenticate(exchange, securityContext, body);
            } catch (Throwable e) {
                IoUtils.safeClose(channel);
                logger.error(e.getMessage(), e);
            }
        }
        return AuthenticationMechanismOutcome.NOT_ATTEMPTED;
    }

    @Override
    public ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
        return new ChallengeResult(true, StatusCodes.OK);
    }

    protected abstract AuthenticationMechanismOutcome doAuthenticate(HttpServerExchange exchange, SecurityContext securityContext, byte[] requestBody);

    private boolean hasBody(long contentLength, HttpString method) {
        if (contentLength == 0) return false;  // if body is empty, skip reading
        return Methods.POST.equals(method) || Methods.PUT.equals(method) || Methods.PATCH.equals(method);
    }

    private byte[] ensureCapacity(int contentLength, byte[] body, int position, int bytesRead) {
        byte[] ensureBody = body;
        if (contentLength >= 0) {
            if (bytesRead + position > contentLength)
                throw new Error("body exceeds expected content length, expected=" + contentLength);
        } else {
            if (body == null) { // undertow buffer is 16k, if there is no content length, in most of cases, it's best just to create exact buffer as first read thru
                ensureBody = new byte[bytesRead];
            } else {
                int newLength = position + bytesRead;   // without content length, position will always be current length,
                byte[] bytes = new byte[newLength];     // just expend to exact read size, which is simplest way for best scenario
                System.arraycopy(body, 0, bytes, 0, position);
                ensureBody = bytes;
            }
        }
        return ensureBody;
    }
}
