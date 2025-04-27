package app.controller;

import core.framework.web.security.AuthStrategy;
import core.framework.web.security.PrincipalDetail;
import core.framework.web.security.annotation.PermissionsRequired;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
    @Autowired
    AuthStrategy authStrategy;

    @PutMapping("/auth")
    public String current(HttpServletRequest httpServletRequest, HttpServletResponse response) {
        PrincipalDetail principalDetail = new PrincipalDetail("1", "test", "test.com", Set.of("1"), httpServletRequest.getRemoteAddr());
        return authStrategy.authenticate(httpServletRequest, response, principalDetail);
    }

    @PermissionsRequired(values = {"1", "2"})
    @GetMapping("/get")
    public void get() {

    }
}
