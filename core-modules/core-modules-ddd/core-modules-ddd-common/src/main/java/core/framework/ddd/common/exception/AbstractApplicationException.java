package core.framework.ddd.common.exception;

import core.framework.kernel.exception.ErrorCodeRuntimeException;

/**
 * @author ebin
 */
public abstract class AbstractApplicationException extends ErrorCodeRuntimeException {
    public AbstractApplicationException(String message) {
        super(message);
    }

    public AbstractApplicationException(String message, String errorCode) {
        super(message, errorCode);
    }

    public AbstractApplicationException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
