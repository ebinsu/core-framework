
package core.framework.security.common.exception;

import core.framework.exception.AbstractApplicationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author ebin
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends AbstractApplicationException {
    public ForbiddenException() {
        super("FORBIDDEN", "FORBIDDEN");
    }
}
