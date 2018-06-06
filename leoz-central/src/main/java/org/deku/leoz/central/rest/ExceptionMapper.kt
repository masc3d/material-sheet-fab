package org.deku.leoz.central.rest

import org.deku.leoz.central.Application
import org.deku.leoz.central.data.isJooqAccessException
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.zalando.problem.Status
import sx.rs.RestProblem
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider

/**
 * Created by masc on 21.04.15.
 */
@Profile(Application.PROFILE_CENTRAL)
@Component
@Provider
class ExceptionMapper : org.deku.leoz.node.rest.ExceptionMapper() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun toResponse(e: Exception): javax.ws.rs.core.Response {
        val status: Int
        val entity: Any?

        when {
            e.isJooqAccessException() -> {
                log.error(e.message)
                status = Response.Status.INTERNAL_SERVER_ERROR.statusCode
                entity = RestProblem(status = Status.INTERNAL_SERVER_ERROR, detail = e.message)
            }
            else -> {
                return super.toResponse(e)
            }
        }

        return Response
                .status(status)
                .entity(entity)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build()
    }
}