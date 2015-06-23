package org.deku.leo2.node.rest;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sx.util.Cast;

import javax.inject.Named;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.Optional;

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
        private String mMessage;

        public ExceptionResult(int status, String message) {
            mStatus = status;
            mMessage = message;
        }

        public ExceptionResult(int status, Exception e) {
            this(status, e.getMessage());
        }

        public Integer getStatus() {
            return mStatus;
        }

        public String getMessage() {
            return mMessage;
        }
    }

    Log mLogger = LogFactory.getLog(ExceptionMapper.class);

    @Override
    public javax.ws.rs.core.Response toResponse(Exception e) {
        ExceptionResult result;
        if (e instanceof WebApplicationException) {
            WebApplicationException we = (WebApplicationException) e;
            result = new ExceptionResult(we.getResponse().getStatus(), we);
        } else if (e instanceof JsonMappingException) {
            JsonMappingException jm = (JsonMappingException) e;
            String locationMessage = String.join(".", (Iterable)jm.getPath().stream().map(p -> p.getFieldName())::iterator);

            result = new ExceptionResult(Response.Status.NOT_FOUND.getStatusCode(),
                    String.format("JSON mapping error [%s]: %s", locationMessage, jm.getCause().getMessage()));
        } else if (e instanceof JsonProcessingException) {
            JsonParseException je = (JsonParseException) e;
            JsonLocation jl = je.getLocation();

            Optional<String> locationMessage = Optional.empty();
            if (jl != null) {
                locationMessage = Optional.of(String.format("in line %d column %d", jl.getLineNr(), jl.getColumnNr()));
            }

            result = new ExceptionResult(Response.Status.NOT_FOUND.getStatusCode(),
                    String.format("JSON parse error%s: %s",
                            locationMessage.orElse(""),
                            je.getOriginalMessage()));
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