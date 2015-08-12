package org.deku.leo2.node.rest;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.rest.entities.v1.Error;

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
    Log mLog = LogFactory.getLog(this.getClass());

    @Override
    public javax.ws.rs.core.Response toResponse(Exception e) {
        Error result;

        if (e instanceof ServiceException) {
            ServiceException se = (ServiceException) e;
            result = new Error(se.getResponse().getStatus(), se.getErrorCode().ordinal(), se.getMessage());

        } else if (e instanceof WebApplicationException) {
            //region WebApplicationException
            WebApplicationException we = (WebApplicationException) e;
            result = new Error(we.getResponse().getStatus(), we);
            //endregion

        } else if (e instanceof JsonMappingException) {
            //region JsonMappingException
            JsonMappingException jm = (JsonMappingException) e;
            String locationMessage = String.join(".", (Iterable)jm.getPath().stream().map(p -> p.getFieldName())::iterator);

            result = new Error(Response.Status.BAD_REQUEST.getStatusCode(),
                    String.format("JSON mapping error [%s]: %s", locationMessage,
                            (jm.getCause() != null) ? jm.getCause().getMessage() : "unknown"));
            //endregion

        } else if (e instanceof JsonProcessingException) {
            //region JsonProcessingException
            JsonProcessingException je = (JsonProcessingException) e;
            JsonLocation jl = je.getLocation();

            Optional<String> locationMessage = Optional.empty();
            if (jl != null) {
                locationMessage = Optional.of(String.format(" in line %d column %d", jl.getLineNr(), jl.getColumnNr()));
            }

            result = new Error(Response.Status.BAD_REQUEST.getStatusCode(),
                    String.format("JSON processing error%s: %s",
                            locationMessage.orElse(""),
                            je.getOriginalMessage()));
            //endregion

        } else {
            result = new Error(Response.Status.BAD_REQUEST.getStatusCode(), e);
        }

        return Response
                .status(result.getStatus())
                .entity(result)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}