package com.framework.web.undertow.security.authentication;

import core.framework.exception.AbstractApplicationException;

/**
 * @author ebin
 */
public class RequestValidFailedException extends AbstractApplicationException {
    public RequestValidFailedException() {
        super("Request valid failed.", "REQUEST_VALID_FAILED");
    }
}
