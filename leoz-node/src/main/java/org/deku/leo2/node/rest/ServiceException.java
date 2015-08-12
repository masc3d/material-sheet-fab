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
     * @param errorCode Error code
     * @param message Message
     * @param cause Exception/throwable
     * @param status HTTP status
     */
    public ServiceException(Enum errorCode, String message, Throwable cause, Response.Status status) {
        super(message, cause, status);
        mErrorCode = errorCode;
    }

    /**
     * c'tor
     * @param errorCode Error code
     * @param message Message
     * @param status HTTP status
     */
    public ServiceException(Enum errorCode, String message, Response.Status status) {
        this(errorCode, message, null, status);
    }

    /**
     * c'tor with HTTP status BAD_REQUEST
     * @param errorCode Error code
     * @param message Message
     */
    public ServiceException(Enum errorCode, String message) {
        this(errorCode, message, null, Response.Status.BAD_REQUEST);
    }

    /**
     * c'tor with HTTP status BAD_REQUEST
     * @param errorCode Error code
     */
    public ServiceException(Enum errorCode) {
        this(errorCode, errorCode.toString(), null, Response.Status.BAD_REQUEST);
    }

    /** Error code */
    public Enum getErrorCode() {
        return mErrorCode;
    }
}
