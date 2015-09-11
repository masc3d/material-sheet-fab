package org.deku.leoz.rest.entities.v1;

import com.wordnik.swagger.annotations.ApiModel;

/**
 * REST service error
 */
@ApiModel(value = "Error", description = "Service error")
public class Error {
    private Integer mHttpStatus;
    private Integer mCode;
    private String mMessage;

    public Error(int httpStatus, Integer code, String message) {
        mHttpStatus = httpStatus;
        mCode = code;
        mMessage = message;
    }

    public Error(int httpStatus, String message) {
        this(httpStatus, null, message);
    }

    public Error(int httpStatus, Exception e) {
        this(httpStatus, e.getMessage());
    }

    public Integer getHttpStatus() {
        return mHttpStatus;
    }

    public String getMessage() {
        return mMessage;
    }

    public Integer getCode() {
        return mCode;
    }
}