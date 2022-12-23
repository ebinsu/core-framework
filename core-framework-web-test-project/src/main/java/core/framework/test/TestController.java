package core.framework.test;

import jakarta.servlet.ServletException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author ebin
 */
@RestController
public class TestController {
    @Anonymous
    @GetMapping("/test")
    public String test() throws ServletException, IOException {
        return "123";
    }

    @GetMapping("/test1")
    public String test1() throws ServletException, IOException {
        return "123";
    }
}
