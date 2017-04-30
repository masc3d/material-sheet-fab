package org.deku.leoz.service.v1

import io.swagger.annotations.*
import org.deku.leoz.service.entity.v1.Routing
import org.deku.leoz.service.entity.v1.RoutingRequest
import org.deku.leoz.service.entity.v1.ServiceError

import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

//import org.deku.leoz.rest.entities.v1.RoutingVia;

/**
 * Created by masc on 17.09.14.
 */
@javax.ws.rs.Path("v1/routing")
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@io.swagger.annotations.Api(value = "Routing operations")
interface RoutingService {
    /**
     * Routing service specific error codes
     */
    enum class ErrorCode private constructor(private val mValue: Int) {
        ROUTE_NOT_AVAILABLE_FOR_GIVEN_PARAMETER(1000)
    }

    @javax.ws.rs.POST
    @javax.ws.rs.Path("/request")
    @io.swagger.annotations.ApiOperation(value = "Request routing information")
    @io.swagger.annotations.ApiResponses(*arrayOf(
            io.swagger.annotations.ApiResponse(code = 400, message = "Bad request/parameter", response = ServiceError::class))
    )
    fun request(@io.swagger.annotations.ApiParam(value = "Routing request") routingRequest: org.deku.leoz.service.entity.v1.RoutingRequest): org.deku.leoz.service.entity.v1.Routing
}
