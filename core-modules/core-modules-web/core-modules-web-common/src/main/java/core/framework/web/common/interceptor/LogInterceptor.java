package core.framework.web.common.interceptor;

import core.framework.kernel.log.Logs;
import core.framework.kernel.log.marker.ErrorCodeMarker;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.time.Duration;

/**
 * @author ebin
 */
public class LogInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogInterceptor.class);
    private static final String START_TIME_ATTRIBUTE = "core.framework.web.common.interceptor.LogInterceptor.startTime";
    private static final String HEADER_TIMEOUT = "timeout";
    private final long maxProcessTimeInNano = Duration.ofSeconds(5).toNanos();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        Logs.action("api:" + request.getMethod().toLowerCase() + ":" + pattern);
        long startTime = System.nanoTime();
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime != null) {
            long elapsed = System.nanoTime() - startTime;
            Logs.put("elapsed", elapsed);
            long maxProcessTimeInNano = maxProcessTime(request.getHeader(HEADER_TIMEOUT));
            if (maxProcessTimeInNano > 0 && elapsed > maxProcessTimeInNano) {
                LOGGER.warn(new ErrorCodeMarker("SLOW_PROCESS"), "http request took longer than of max process time, maxProcessTime={}, elapsed={}", Duration.ofNanos(maxProcessTimeInNano), Duration.ofNanos(elapsed));
            }
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
