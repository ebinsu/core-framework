package core.framework.test.web;

import core.framework.web.security.annotation.Anonymous;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ebin
 */
@RestController
public class TestController {

    @Anonymous
    @GetMapping("/test")
    public @ResponseBody String test() {
        return "Hello, World";
    }
}
