package app.controller;

import core.framework.web.security.CurrentUserRepository;
import core.framework.web.security.CurrentUser;
import core.framework.web.security.annotation.PermissionsRequired;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Set;

/**
 * @author ebin
 */
@RestController
@RequestMapping("/test")
public class JWTTestController {
    @Autowired
    @Qualifier("jwtCurrentUserRepository")
    CurrentUserRepository currentUserRepository;

    @PutMapping("/auth")
    public String current(HttpServletRequest httpServletRequest) {
        CurrentUser currentUser = new CurrentUser("1", "test", "test.com", Set.of("1"));
        return currentUserRepository.save(currentUser);
    }

    @PermissionsRequired(values = {"1", "2"})
    @GetMapping("/get")
    public void get(CurrentUser currentUser) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        System.out.println(currentUser);
    }
}
