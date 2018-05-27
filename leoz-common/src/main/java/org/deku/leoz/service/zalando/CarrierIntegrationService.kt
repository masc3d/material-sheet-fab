package org.deku.leoz.service.zalando

import io.swagger.annotations.*
import org.deku.leoz.service.zalando.entity.DeliveryOption
import org.deku.leoz.service.zalando.entity.DeliveryOrder
import org.deku.leoz.service.zalando.entity.NotifiedDeliveryOrder
import org.deku.leoz.service.zalando.entity.Problem
import sx.rs.auth.ApiKey
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Created by 27694066 on 02.03.2017.
 */
@Path("zalando/v1/ldn")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Carrier integration operations")
@ApiKey(true)
interface CarrierIntegrationService {

    @GET
    @Path("/delivery-options")
    @ApiOperation(
            value = "Provides available delivery options for a given pair of addresses (source, target).",
            notes = "Looks up for available delivery options that apply to the path from source address to target address. Provides a list of options which detail the window of delivery and the cut-off and pick-up points.",
            response = DeliveryOption::class,
            responseContainer = "List"
    )
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
    ): List<DeliveryOption>

    @POST
    @Path("delivery-orders")
    @ApiOperation(
            value = "Send order information",
            response = NotifiedDeliveryOrder::class
    )
    @ApiResponses(*arrayOf(
            ApiResponse(code = 200, message = "OK", response = NotifiedDeliveryOrder::class),
            ApiResponse(code = 400, message = "Bad Request", response = Problem::class),
            ApiResponse(code = 401, message = "Unauthorized", response = Problem::class))
    )
    fun postDeliveryOrder(
            @ApiParam(value = "DeliveryOrder") deliveryOrder: DeliveryOrder
    ): NotifiedDeliveryOrder

    @POST
    @Path("delivery-orders/{id}/cancellation")
    @ApiOperation(
            value = "Cancel a delivery order"
    )
    @ApiResponses(*arrayOf(
            ApiResponse(code = 200, message = "OK"),
            ApiResponse(code = 400, message = "Bad Request", response = Problem::class),
            ApiResponse(code = 401, message = "Unauthorized", response = Problem::class)
        )
    )
    fun cancelDeliveryOrder(
            @PathParam(value = "id") @ApiParam(example = "1234567890", value = "Order identifier") id: String
    ): Response
}