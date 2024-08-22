package com.framework.shared.log;

import core.framework.shared.log.LogAttribute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

/**
 * @author ebin
 */
class LogAttributeTest {
    @Test
    void test() {
        LogAttribute.info("1", "1");
        LogAttribute.info("1", "2");
        String s = LogAttribute.get("1");
        Assertions.assertEquals(s, "1 , 2");
        LogAttribute.end();
    }

    @Test
    void test1() {
        LogAttribute.info("1", "1", "2");
        LogAttribute.info("1", "2");
        String s = LogAttribute.get("1");
        Assertions.assertEquals(s, "[1,2] , 2");
        LogAttribute.end();
    }

    @Test
    void test2() {
        LogAttribute.info("1", "1", "2");
        LogAttribute.info("1", "2 =");
        String s = LogAttribute.get("1");
        Assertions.assertEquals(s, "[1,2] , 2 *");
        LogAttribute.end();
    }
}
