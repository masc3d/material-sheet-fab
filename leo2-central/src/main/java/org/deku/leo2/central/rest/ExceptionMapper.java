package org.deku.leo2.central.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by masc on 21.04.15.
 */
@Provider
public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Exception> {
    Logger mLogger = Logger.getLogger(ExceptionMapper.class.getName());

    @Override
    public javax.ws.rs.core.Response toResponse(Exception e) {
        mLogger.log(Level.SEVERE, e.getMessage(), e);
        if (e instanceof WebApplicationException) {
            return ((WebApplicationException) e).getResponse();
        } else {
            return null;
        }
    }
}