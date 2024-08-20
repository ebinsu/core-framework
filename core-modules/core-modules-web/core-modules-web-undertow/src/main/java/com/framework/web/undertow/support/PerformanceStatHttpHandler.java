package com.framework.web.undertow.support;

import core.framework.exception.marker.ErrorCodeMarker;
import core.framework.shared.utils.StopWatch;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.Duration;

/**
 * @author ebin
 */
public class PerformanceStatHttpHandler implements HttpHandler {
    public static final HttpString HEADER_TIMEOUT = new HttpString("timeout");
    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceStatHttpHandler.class);
    private final long maxProcessTimeInNano = Duration.ofSeconds(10).toNanos();
    private final HttpHandler next;

    public PerformanceStatHttpHandler(HttpHandler next) {
        this.next = next;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        long maxProcessTimeInNano = maxProcessTime(exchange.getRequestHeaders().getFirst(HEADER_TIMEOUT));
        long httpDelay = System.nanoTime() - exchange.getRequestStartTime();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("httpDelay={}", httpDelay);
        }
        MDC.put("http_delay", String.valueOf(httpDelay));
        StopWatch stopWatch = new StopWatch();
        next.handleRequest(exchange);
        long elapsed = stopWatch.elapsed();
        if (maxProcessTimeInNano > 0 && elapsed > maxProcessTimeInNano) {
            LOGGER.warn(new ErrorCodeMarker("SLOW_PROCESS"), "http request took longer than of max process time, maxProcessTime={}, elapsed={}", Duration.ofNanos(maxProcessTimeInNano), Duration.ofNanos(elapsed));
        }
    }

    private long maxProcessTime(String timeout) {
        if (timeout != null) {
            try {
                return Long.parseLong(timeout);
            } catch (NumberFormatException e) {
                // ignore if got invalid timeout header from internet
                return maxProcessTimeInNano;
            }
        }
        return maxProcessTimeInNano;
    }
}
