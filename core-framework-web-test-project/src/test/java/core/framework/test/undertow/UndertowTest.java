package core.framework.test.undertow;

import org.junit.jupiter.api.Test;

/**
 * @author ebin
 */
class UndertowTest {
    @Test
    void test_session_to_json() {
        new RedisSessionManager.SessionImpl("1", 1, null, null);
    }
}
