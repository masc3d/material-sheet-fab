package org.deku.leoz.rest.service.v1

import io.swagger.annotations.*
import org.deku.leoz.rest.entity.v1.Routing
import org.deku.leoz.rest.entity.v1.RoutingRequest
import org.deku.leoz.rest.entity.v1.DeliveryOption
import org.deku.leoz.rest.entity.v1.Problem
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Created by 27694066 on 02.03.2017.
 */
@Path("v1/ldn")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Zalando LDN operations")
interface ZalandoService {

    @GET
    @Path("/delivery-options")
    @ApiOperation(value = "Request delivery information")
    @ApiResponses(*arrayOf(
            ApiResponse(code = 200, message = "OK", response = DeliveryOption::class),
            ApiResponse(code = 400, message = "Bad Request", response = Problem::class),
            ApiResponse(code = 401, message= "Unauthorized", response = Problem::class))
    )
    fun requestDeliveryOption(
            @ApiParam(value = "source_address.country_code", required = true) source_address_country_code: String,
            @ApiParam(value = "source_address.city", required = true) source_address_city: String,
            @ApiParam(value = "source_address.zip_code", required = true) source_address_zip_code: String,
            @ApiParam(value = "source_address.address_line", required = true) source_address_address_line: String,
            @ApiParam(value = "target_address.country_code", required = true) target_address_country_code: String,
            @ApiParam(value = "target_address.city", required = true) target_address_city: String,
            @ApiParam(value = "target_address.zip_code", required = true) target_address_zip_code: String,
            @ApiParam(value = "target_address.address_line", required = true) target_address_address_line: String
    )
}