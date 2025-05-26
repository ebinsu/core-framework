package com.framework.web.undertow.support;

import core.framework.kernel.log.marker.ErrorCodeMarker;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StopWatch;

import java.time.Duration;

/**
 * @author ebin
 */
public class LogAttributeHttpHandler implements HttpHandler {
    public static final HttpString HEADER_TIMEOUT = new HttpString("timeout");
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAttributeHttpHandler.class);
    private final long maxProcessTimeInNano = Duration.ofSeconds(10).toNanos();
    private final HttpHandler next;

    public LogAttributeHttpHandler(HttpHandler next) {
        this.next = next;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        long maxProcessTimeInNano = maxProcessTime(exchange.getRequestHeaders().getFirst(HEADER_TIMEOUT));
        long httpDelay = System.nanoTime() - exchange.getRequestStartTime();
        MDC.put("http_delay", String.valueOf(httpDelay));
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        next.handleRequest(exchange);
        stopWatch.stop();
        long elapsed = stopWatch.getTotalTimeNanos();
        MDC.put("elapsed", String.valueOf(elapsed));
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
