package org.deku.leo2.node.rest.services;

/**
 * Created by JT on 12.08.15.
 */
public class ErrorCodes {

    public enum restErrorCodes
    {
        MISSING_PARAMETER("1001"),
        ROUTE_NOT_AVAILABLE_FOR_GIVEN_PARAMETER("1002"),
        WRONG_PARAMETER_VALUE("1003")
        ;

        String mErrorCode;
        restErrorCodes (String errorCode) { mErrorCode=errorCode;}
    }
}