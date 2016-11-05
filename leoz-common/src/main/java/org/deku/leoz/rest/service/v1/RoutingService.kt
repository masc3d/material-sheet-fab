package org.deku.leoz.rest.service.v1

import io.swagger.annotations.*
import org.deku.leoz.rest.entity.v1.Routing
import org.deku.leoz.rest.entity.v1.RoutingRequest

import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.MediaType

//import org.deku.leoz.rest.entities.v1.RoutingVia;

/**
 * Created by masc on 17.09.14.
 */
@Path("v1/routing")
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
            ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun request(@ApiParam(value = "Routing request") routingRequest: RoutingRequest): Routing
}
