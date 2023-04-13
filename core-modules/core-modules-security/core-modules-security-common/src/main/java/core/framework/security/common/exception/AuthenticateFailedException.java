package core.framework.security.common.exception;

import core.framework.exception.AbstractApplicationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author ebin
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AuthenticateFailedException extends AbstractApplicationException {
    public AuthenticateFailedException() {
        super("登录失败", "AUTHENTICATE_FAILED");
    }
}
