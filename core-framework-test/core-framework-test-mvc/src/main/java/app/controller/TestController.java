package app.controller;

import app.service.TestService;
import core.framework.exception.ErrorCodeRuntimeException;
import core.framework.web.security.AuthStrategy;
import core.framework.web.security.PrincipalDetail;
import core.framework.web.security.annotation.Anonymous;
import core.framework.web.security.annotation.PermissionsRequired;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * @author ebin
 */
@RestController
@RequestMapping("/test")
public class TestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);
    @Autowired
    AuthStrategy authStrategy;
    @Autowired
    TestService testService;

    @PutMapping("/auth")
    public String current(HttpServletRequest httpServletRequest, HttpServletResponse response) {
        PrincipalDetail principalDetail = new PrincipalDetail("1", "test", "test.com", Set.of("1"), httpServletRequest.getRemoteAddr());
        return authStrategy.authenticate(httpServletRequest, response, principalDetail);
    }

    @PermissionsRequired(values = {"1", "2"})
    @GetMapping("/get")
    public void get() {

    }

    @Anonymous
    @GetMapping("/error")
    public void error() {
        throw new ErrorCodeRuntimeException("TEST", "test");
    }

    @Anonymous
    @GetMapping("/{userId}/get")
    public void getByUserId(@PathVariable String userId) {
        LOGGER.info("controller");
        testService.test(userId);

    }
}
