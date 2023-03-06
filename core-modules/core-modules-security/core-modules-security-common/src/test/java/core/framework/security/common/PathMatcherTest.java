package core.framework.security.common;

import org.springframework.util.AntPathMatcher;

/**
 * @author ebin
 */
public class PathMatcherTest {
    public static void main(String[] args) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        System.out.println(antPathMatcher.match("bo/**", "bo/order/xxx"));
    }
}
