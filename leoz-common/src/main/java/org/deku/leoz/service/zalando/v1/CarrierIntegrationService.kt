package org.deku.leoz.service.zalando.v1

import io.swagger.annotations.*
import org.deku.leoz.service.entity.zalando.v1.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Created by 27694066 on 02.03.2017.
 */
@javax.ws.rs.Path("zalando/v1/ldn")
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@io.swagger.annotations.Api(value = "Carrier integration operations")
interface CarrierIntegrationService {

    @javax.ws.rs.GET
    @javax.ws.rs.Path("/delivery-options")
    @io.swagger.annotations.ApiOperation(
            value = "Provides available delivery options for a given pair of addresses (source, target).",
            notes = "Looks up for available delivery options that apply to the path from source address to target address. Provides a list of options which detail the window of delivery and the cut-off and pick-up points.",
            response = org.deku.leoz.service.entity.zalando.v1.DeliveryOption::class,
            responseContainer = "List"
    )
    @io.swagger.annotations.ApiResponses(*arrayOf(
            io.swagger.annotations.ApiResponse(code = 200, message = "OK", response = DeliveryOption::class),
            io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = Problem::class),
            io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized", response = Problem::class))
    )
    fun requestDeliveryOption(
            @javax.ws.rs.QueryParam(value = "source_address.country_code") source_address_country_code: String,
            @javax.ws.rs.QueryParam(value = "source_address.city") source_address_city: String,
            @javax.ws.rs.QueryParam(value = "source_address.zip_code") source_address_zip_code: String,
            @javax.ws.rs.QueryParam(value = "source_address.address_line") source_address_address_line: String,
            @javax.ws.rs.QueryParam(value = "target_address.country_code") target_address_country_code: String,
            @javax.ws.rs.QueryParam(value = "target_address.city") target_address_city: String,
            @javax.ws.rs.QueryParam(value = "target_address.zip_code") target_address_zip_code: String,
            @javax.ws.rs.QueryParam(value = "target_address.address_line") target_address_address_line: String
    ): List<org.deku.leoz.service.entity.zalando.v1.DeliveryOption>

    @javax.ws.rs.POST
    @javax.ws.rs.Path("delivery-orders")
    @io.swagger.annotations.ApiOperation(
            value = "Send order information",
            response = org.deku.leoz.service.entity.zalando.v1.NotifiedDeliveryOrder::class
    )
    @io.swagger.annotations.ApiResponses(*arrayOf(
            io.swagger.annotations.ApiResponse(code = 200, message = "OK", response = NotifiedDeliveryOrder::class),
            io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = Problem::class),
            io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized", response = Problem::class))
    )
    fun postDeliveryOrder(
            @io.swagger.annotations.ApiParam(value = "DeliveryOrder") deliveryOrder: org.deku.leoz.service.entity.zalando.v1.DeliveryOrder
    ): org.deku.leoz.service.entity.zalando.v1.NotifiedDeliveryOrder

    @javax.ws.rs.POST
    @javax.ws.rs.Path("delivery-orders/{id}/cancellation")
    @io.swagger.annotations.ApiOperation(
            value = "Cancel a delivery order"
    )
    @io.swagger.annotations.ApiResponses(*arrayOf(
            io.swagger.annotations.ApiResponse(code = 200, message = "OK"),
            io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = Problem::class),
            io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized", response = Problem::class)
        )
    )
    fun cancelDeliveryOrder(
            @javax.ws.rs.PathParam(value = "id") @io.swagger.annotations.ApiParam(example = "1234567890", value = "Order identifier") id: String
    ): javax.ws.rs.core.Response
}