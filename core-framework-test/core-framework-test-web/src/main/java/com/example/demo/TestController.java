package com.example.demo;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;

/**
 * @author ebin
 */
@RestController
@RequestMapping("/test")
public class TestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/1")
    public Map<String, String> current() {
        MDC.put("1", "2");
        LOGGER.warn(new ErrorCodeMarker("1"), "test {}", 1);
        LOGGER.error(new ErrorCodeMarker("2"), "test {}", 2);
        return Map.of("1", "2");
    }

    @GetMapping("/2")
    public Map<String, String> current2() {
        LOGGER.error(new ErrorCodeMarker("2"), "test {}", 2);
        LOGGER.warn(new ErrorCodeMarker("1"), "test {}", 2);
        return Map.of("1", "2");
    }

    @GetMapping("/3")
    public Map<String, String> current3() {
        throw new RuntimeException("xx");
    }

    @GetMapping("/4")
    public Map<String, String> current4(HttpServletRequest request) {
        System.out.println(Arrays.toString(request.getCookies()));
        System.out.println(request.getUserPrincipal());
        return Map.of("1", "2");
    }

    @GetMapping("/5")
    public Map<String, String> current5(HttpServletRequest request) {
        try {
            Thread.sleep(11000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Map.of("1", "2");
    }
}
