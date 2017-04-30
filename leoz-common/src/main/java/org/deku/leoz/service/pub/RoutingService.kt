package org.deku.leoz.service.pub

import org.deku.leoz.service.entity.pub.ServiceError
import javax.ws.rs.*
import javax.ws.rs.core.*
import io.swagger.annotations.*
import org.deku.leoz.service.entity.pub.Routing
import org.deku.leoz.service.entity.pub.RoutingRequest

/**
 * Created by masc on 17.09.14.
 */
@Path("v1/routing")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Routing operations")
interface RoutingService {
    /**
     * Routing service specific error codes
     */
    enum class ErrorCode private constructor(private val mValue: Int) {
        ROUTE_NOT_AVAILABLE_FOR_GIVEN_PARAMETER(1000)
    }

    @POST
    @Path("/request")
    @ApiOperation(value = "Request routing information")
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = ServiceError::class))
    )
    fun request(@ApiParam(value = "Routing request") routingRequest: RoutingRequest): Routing
}
