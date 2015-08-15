package org.deku.leoz.rest.entities.v1;

import com.wordnik.swagger.annotations.ApiModel;

/**
 * REST service error
 */
@ApiModel(value = "Error", description = "Service error")
public class Error {
    private Integer mStatus;
    private Integer mCode;
    private String mMessage;

    public Error(int status, Integer code, String message) {
        mStatus = status;
        mCode = code;
        mMessage = message;
    }

    public Error(int status, String message) {
        this(status, null, message);
    }

    public Error(int status, Exception e) {
        this(status, e.getMessage());
    }

    public Integer getStatus() {
        return mStatus;
    }

    public String getMessage() {
        return mMessage;
    }

    public Integer getCode() {
        return mCode;
    }
}