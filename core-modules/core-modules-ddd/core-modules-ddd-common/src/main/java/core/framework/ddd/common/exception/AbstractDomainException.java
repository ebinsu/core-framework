package core.framework.ddd.common.exception;

import core.framework.kernel.exception.ErrorCodeRuntimeException;

/**
 * @author ebin
 */
public abstract class AbstractDomainException extends ErrorCodeRuntimeException {
    public AbstractDomainException(String message) {
        super(message);
    }

    public AbstractDomainException(String message, String errorCode) {
        super(message, errorCode);
    }

    public AbstractDomainException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
