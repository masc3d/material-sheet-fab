package org.deku.leoz.service.pub

import io.swagger.annotations.*
import org.deku.leoz.service.internal.OrderService
import sx.rs.auth.ApiKey
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("v1/document")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Document service")
@ApiKey(false)
interface DocumentService {
    companion object {
        const val PARCELID = "parcel-id"
        const val PARCELNO = "parcel-no"
    }

    @ApiModel(value = "LabelRequest", description = "Request for label print")
    class LabelRequest {

        @ApiModel(value = "LabelParticipant", description = "Request participant. Consignor or consignee")
        class LabelParticipant {
            @ApiModelProperty(position = 10, required = true, value = "Station Number")
            var stationNo: String? = null
            @ApiModelProperty(position = 20, required = true, value = "Name 1")
            var name1: String? = null
            @ApiModelProperty(position = 30, required = false, value = "Name 2")
            var name2: String? = null
            @ApiModelProperty(position = 40, required = true, value = "Street")
            var street: String? = null
            @ApiModelProperty(position = 50, required = true, value = "City")
            var city: String? = null
            @ApiModelProperty(position = 60, required = true, value = "Zip code")
            var zipCode: String? = null
            @ApiModelProperty(position = 70, required = false, value = "Phone")
            var phone: String? = null
            @ApiModelProperty(position = 80, required = false, value = "Notice")
            var notice: String? = null
        }

        @ApiModelProperty(position = 10, required = true, value = "Parcel ID")
        var parcelId: Long? = null
        @ApiModelProperty(position = 20, required = true, value = "Order ID")
        var orderId: Long? = null
        @ApiModelProperty(position = 30, required = true, value = "Consignor")
        var consignor: LabelParticipant? = null
        @ApiModelProperty(position = 40, required = true, value = "Consignee")
        var consignee: LabelParticipant? = null
        @ApiModelProperty(position = 50, required = true, value = "Appointment")
        var appointment: OrderService.Order.Appointment = OrderService.Order.Appointment()
    }

    @GET
    @Path("/label")
    @ApiOperation(value = "Get Label-Document")
    @Produces(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM)
    fun printParcelLabel(
            @ApiParam(value = "Label request", required = false) labelRequest: LabelRequest? = null,
            @QueryParam(PARCELID) @ApiParam(value = "Parcel ID", required = false) parcelId: Long? = null,
            @QueryParam(PARCELNO) @ApiParam(value = "Parcel Number", required = false) parcelNo: String? = null
    )
}