package org.deku.leoz.rest.service.zalando.v1

import io.swagger.annotations.*
import org.deku.leoz.rest.entity.zalando.v1.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Created by 27694066 on 02.03.2017.
 */
@Path("v1/ldn")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Carrier integration operations")
interface CarrierIntegrationService {

    @GET
    @Path("/delivery-options")
    @ApiOperation(value = "Request delivery information")
    @ApiResponses(*arrayOf(
            ApiResponse(code = 200, message = "OK", response = DeliveryOption::class),
            ApiResponse(code = 400, message = "Bad Request", response = Problem::class),
            ApiResponse(code = 401, message = "Unauthorized", response = Problem::class))
    )
    fun requestDeliveryOption(
            @QueryParam(value = "source_address.country_code") source_address_country_code: String,
            @QueryParam(value = "source_address.city") source_address_city: String,
            @QueryParam(value = "source_address.zip_code") source_address_zip_code: String,
            @QueryParam(value = "source_address.address_line") source_address_address_line: String,
            @QueryParam(value = "target_address.country_code") target_address_country_code: String,
            @QueryParam(value = "target_address.city") target_address_city: String,
            @QueryParam(value = "target_address.zip_code") target_address_zip_code: String,
            @QueryParam(value = "target_address.address_line") target_address_address_line: String
    )

    @POST
    @Path("delivery-orders")
    @ApiOperation(value = "Send order information")
    @ApiResponses(*arrayOf(
            ApiResponse(code = 200, message = "OK", response = NotifiedDeliveryOrder::class),
            ApiResponse(code = 400, message = "Bad Request", response = Problem::class),
            ApiResponse(code = 401, message = "Unauthorized", response = Problem::class))
    )
    fun postDeliveryOrder(
            @ApiParam(value = "DeliveryOrder") deliveryOrder: DeliveryOrder
    )
}