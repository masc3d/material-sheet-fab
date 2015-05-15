package org.deku.leo2.central.rest;

import sx.util.Cast;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by masc on 21.04.15.
 */
@Provider
public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Exception> {
    /**
     * Represents webservice result on exception
     */
    static class ExceptionResult {
        private int mStatus;
        private Exception mException;

        public ExceptionResult(int status, Exception e) {
            mException = e;
            mStatus = status;
        }

        public Integer getStatus() {
            return mStatus;
        }

        public String getMessage() {
            return mException.getMessage();
        }

        public String getLocalizedMessage() {
            return mException.getLocalizedMessage();
        }
    }

    Logger mLogger = Logger.getLogger(ExceptionMapper.class.getName());

    @Override
    public javax.ws.rs.core.Response toResponse(Exception e) {
        mLogger.log(Level.SEVERE, e.getMessage(), e);
        WebApplicationException we = Cast.as(WebApplicationException.class, e);

        ExceptionResult result;
        if (we != null) {
            result = new ExceptionResult(we.getResponse().getStatus(), we);
        } else {
            result = new ExceptionResult(Response.Status.NOT_FOUND.getStatusCode(), e);
        }

        return Response
                .status(result.getStatus())
                .entity(result)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}