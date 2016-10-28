package org.deku.leoz.node.rest

import com.fasterxml.jackson.core.JsonLocation
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import org.deku.leoz.rest.entities.v1.Error
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Named
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider
import java.util.Optional

/**
 * Created by masc on 21.04.15.
 */
@Named
@Provider
class ExceptionMapper : javax.ws.rs.ext.ExceptionMapper<Exception> {
    internal var mLog = LoggerFactory.getLogger(this.javaClass)

    override fun toResponse(e: Exception): javax.ws.rs.core.Response {
        val result: Error

        if (e is ServiceException) {
            result = Error(e.response.status, e.errorCode.ordinal, e.message)

        } else if (e is WebApplicationException) {
            //region WebApplicationException
            result = Error(e.response.status, e)
            //endregion

        } else if (e is JsonMappingException) {
            //region JsonMappingException
            val locationMessage = (Iterable<*> { e.path.stream().map({ p -> p.getFieldName() }).iterator() } as Iterable<*>).joinToString(".")

            result = Error(Response.Status.BAD_REQUEST.statusCode,
                    String.format("JSON mapping error [%s]: %s", locationMessage,
                            if (e.cause != null) e.cause.message else "unknown"))
            //endregion

        } else if (e is JsonProcessingException) {
            //region JsonProcessingException
            val jl = e.location

            var locationMessage = Optional.empty<String>()
            if (jl != null) {
                locationMessage = Optional.of(String.format(" in line %d column %d", jl.lineNr, jl.columnNr))
            }

            result = Error(Response.Status.BAD_REQUEST.statusCode,
                    String.format("JSON processing error%s: %s",
                            locationMessage.orElse(""),
                            e.originalMessage))
            //endregion

        } else {
            mLog.error(e.message, e)
            result = Error(Response.Status.BAD_REQUEST.statusCode, e)
        }

        return Response.status(result.httpStatus!!).entity(result).type(MediaType.APPLICATION_JSON_TYPE).build()
    }
}