package org.deku.leo2.node.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * REST service exception
 * Created by masc on 11.08.15.
 */
public class ServiceException extends WebApplicationException {
    private Enum mErrorCode;

    /**
     * c'tor
     * @param message
     * @param cause
     * @param status
     * @param errorCode
     */
    public ServiceException(Enum errorCode, String message, Throwable cause, Response.Status status) {
        super(message, cause, status);
        mErrorCode = errorCode;
    }

    public ServiceException(Enum code) {
        this(code, code.toString(), null, Response.Status.NOT_FOUND);
    }

    public ServiceException(Enum code, String message) {
        this(code, message, null, Response.Status.NOT_FOUND);
    }

    public Enum getErrorCode() {
        return mErrorCode;
    }
}
