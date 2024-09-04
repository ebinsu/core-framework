import com.framework.web.undertow.security.authentication.mailecode.EmailCodeAuthMechanism;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author ebin
 */
class EmailTest {

    @Test
    void test() {
        Assertions.assertTrue(EmailCodeAuthMechanism.isValidEmail("601054510@qq.com"));
    }
}
