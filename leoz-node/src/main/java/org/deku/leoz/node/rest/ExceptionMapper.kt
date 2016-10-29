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
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun toResponse(e: Exception): javax.ws.rs.core.Response {
        val result: Error

        if (e is ServiceException) {
            result = Error(e.response.status, e.errorCode.ordinal, e.message ?: "")

        } else if (e is WebApplicationException) {
            //region WebApplicationException
            result = Error(e.response.status, e)
            //endregion

        } else if (e is JsonMappingException) {
            //region JsonMappingException
            val locationMessage = e.path.map { it.fieldName }.joinToString(".")
            val cause = e.cause?.message ?: "unknown"

            result = Error(Response.Status.BAD_REQUEST.statusCode,
                    "JSON mapping error [${locationMessage}]: ${cause}")
            //endregion

        } else if (e is JsonProcessingException) {
            //region JsonProcessingException
            val jl = e.location

            val locationMessage: String = if (jl != null) " in line ${jl.lineNr} column ${jl.columnNr}" else ""
            result = Error(Response.Status.BAD_REQUEST.statusCode,
                    "JSON processing error${locationMessage}: ${e.originalMessage}")
            //endregion

        } else {
            log.error(e.message, e)
            result = Error(Response.Status.BAD_REQUEST.statusCode, e)
        }

        return Response.status(result.httpStatus!!).entity(result).type(MediaType.APPLICATION_JSON_TYPE).build()
    }
}