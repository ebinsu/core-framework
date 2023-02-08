package core.framework.test.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author ebin
 */
@RestController
public class TestController {
    @GetMapping("/test")
    public @ResponseBody String test() {
        return "Hello, World";
    }

    @GetMapping("/test1")
    public @ResponseBody String test1(@RequestBody Map<String, Object> map) {
        return "Hello, World";
    }
}
