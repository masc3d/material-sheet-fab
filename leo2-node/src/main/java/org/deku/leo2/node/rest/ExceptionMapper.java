package org.deku.leo2.node.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sx.util.Cast;

import javax.inject.Named;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Created by masc on 21.04.15.
 */
@Named
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

    Log mLogger = LogFactory.getLog(ExceptionMapper.class);

    @Override
    public javax.ws.rs.core.Response toResponse(Exception e) {
        mLogger.error(e.getMessage(), e);
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