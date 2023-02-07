package core.framework.security.undertow;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.servlet.spec.HttpServletRequestImpl;
import io.undertow.servlet.spec.HttpServletResponseImpl;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import org.springframework.http.MediaType;

/**
 * @author ebin
 */
public class AJAXAuthenticationHttpHandler implements HttpHandler {
    private final HttpHandler next;

    public AJAXAuthenticationHttpHandler(HttpHandler next) {
        this.next = next;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (isAuthenticationRequest(exchange)) {
            int contentLength = (int) exchange.getRequestContentLength();
            String contentTypeStr = exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE);
            MediaType contentType = contentTypeStr == null ? null : MediaType.valueOf(contentTypeStr);
            if (hasBody(contentLength, exchange.getRequestMethod()) && MediaType.APPLICATION_JSON.equals(contentType)) {
                ServletRequestContext servletRequestContext = exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
                HttpServletRequestImpl originalRequest = servletRequestContext.getOriginalRequest();
                HttpServletResponseImpl originalResponse = servletRequestContext.getOriginalResponse();
                originalRequest.authenticate(originalResponse);
                exchange.endExchange();
                return;
            }
        }
        next.handleRequest(exchange);
    }

    private boolean isAuthenticationRequest(HttpServerExchange exchange) {
        return exchange.getRequestPath().equals("/login");
    }

    private boolean hasBody(long contentLength, HttpString method) {
        if (contentLength == 0) return false;  // if body is empty, skip reading
        return Methods.POST.equals(method) || Methods.PUT.equals(method) || Methods.PATCH.equals(method);
    }

    public static class Wrapper implements HandlerWrapper {

        @Override
        public HttpHandler wrap(HttpHandler handler) {
            return new AJAXAuthenticationHttpHandler(handler);
        }
    }
}
