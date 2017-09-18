package org.deku.leoz.node.rest

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import org.deku.leoz.service.entity.ServiceError
import org.slf4j.LoggerFactory
import org.zalando.problem.ThrowableProblem
import javax.inject.Named
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider

/**
 * Created by masc on 21.04.15.
 */
@Named
@Provider
class ExceptionMapper : javax.ws.rs.ext.ExceptionMapper<Exception> {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun toResponse(e: Exception): javax.ws.rs.core.Response {
        val status: Int
        val entity: Any?

        when(e) {
            is WebApplicationException -> return e.response

            is ServiceException -> {
                if (e.entity != null) {
                    entity = e.entity
                }
                else if (e.errorCode != null) {
                    entity = ServiceError(e.errorCode, e.message ?: "")
                } else {
                    entity = null
                }
                status = e.status.statusCode
            }
            is ThrowableProblem -> {
                entity = e
                status = e.status?.statusCode ?: Response.Status.BAD_REQUEST.statusCode

            }
            is JsonMappingException -> {
                //region JsonMappingException
                log.error(e.message)
                val locationMessage = e.path.map { it.fieldName }.joinToString(".")
                val cause = e.cause?.message ?: "unknown"

                status = Response.Status.BAD_REQUEST.statusCode
                entity = ServiceError(message = "JSON mapping error [${locationMessage}]: ${cause}")
                //endregion
            }
            is JsonProcessingException -> {
                //region JsonProcessingException
                log.error(e.message)
                val jl = e.location

                val locationMessage: String = if (jl != null) " in line ${jl.lineNr} column ${jl.columnNr}" else ""
                status = Response.Status.BAD_REQUEST.statusCode
                entity = ServiceError(message = "JSON processing error${locationMessage}: ${e.originalMessage}")
                //endregion
            }
            else -> {
                log.error(e.message, e)
                status = Response.Status.BAD_REQUEST.statusCode
                entity = ServiceError(cause = e)
            }
        }

        return Response
                .status(status)
                .entity(entity)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build()
    }
}