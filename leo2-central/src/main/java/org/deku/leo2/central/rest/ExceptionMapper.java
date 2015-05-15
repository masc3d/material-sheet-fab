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
    static class ExceptionResponse {
        private WebApplicationException mException;

        public ExceptionResponse(WebApplicationException e) {
            mException = e;
        }

        public Integer getStatus() {
            return mException.getResponse().getStatus();
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
        if (we != null) {
            return Response
                    .status(we.getResponse().getStatus())
                    .entity(new ExceptionResponse(we))
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        } else {
            return null;
        }
    }
}