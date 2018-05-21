package org.deku.leoz.service.pub

import io.swagger.annotations.*
import org.deku.leoz.service.internal.OrderService
import sx.rs.auth.ApiKey
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("v1/document")
@Produces(MediaType.APPLICATION_OCTET_STREAM)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Document service")
@ApiKey(false)
interface DocumentService {
    companion object {
        const val PARCELID = "parcel-id"
        const val PARCELNO = "parcel-no"
        const val RETURN_TYPE = "return-type"
        const val BASE64 = "base64"
        const val PDF = "pdf"
    }

    @ApiModel(value = "LabelRequest", description = "Request for label print", subTypes = [(LabelRequest.LabelParticipant::class)])
    class LabelRequest {

        @ApiModel(value = "LabelParticipant", description = "Request participant. Consignor or consignee")
        class LabelParticipant {
            @ApiModelProperty(position = 10, required = true, value = "Station Number", example = "100")
            var stationNo: String = "100"
            @ApiModelProperty(position = 20, required = true, value = "Name 1", example = "Max Mustermann")
            var name1: String = "Max Mustermann"
            @ApiModelProperty(position = 30, required = false, value = "Name 2", example = "Name2")
            var name2: String? = null
            @ApiModelProperty(position = 40, required = false, value = "Name 3", example = "Name3")
            var name3: String? = null
            @ApiModelProperty(position = 50, required = true, value = "Street", example = "Dörrwiese")
            var street: String = "Doerrwiese"
            @ApiModelProperty(position = 60, required = true, value = "Street-No", example = "2")
            var streetNo: String = "2"
            @ApiModelProperty(position = 70, required = true, value = "City", example = "Neuenstein")
            var city: String = "Neuenstein"
            @ApiModelProperty(position = 80, required = true, value = "Zip code", example = "36286")
            var zipCode: String = "36286"
            @ApiModelProperty(position = 90, required = true, value = "Country code", example = "DE")
            var country: String = "DE"
            @ApiModelProperty(position = 100, required = false, value = "Phone", example = "+49 6677 950")
            var phone: String? = null
            @ApiModelProperty(position = 110, required = false, value = "Notice", example = "Hinten klingeln")
            var notice: String? = null
        }

        @ApiModelProperty(position = 10, required = true, value = "Parcel Number", notes = "Without check digit", example = "10010000002")
        var parcelNumber: String = "10010000001"
        @ApiModelProperty(position = 20, required = true, value = "Order Number", notes = "Without check digit", example = "10010000001")
        var orderNumber: String = "10010000001"
        @ApiModelProperty(position = 30, required = true, value = "Client Station Number", example = "999")
        var clientStationNo: String = "999"
        @ApiModelProperty(position = 40, required = true, value = "Consignor")
        var consignor: LabelParticipant = LabelParticipant()
        @ApiModelProperty(position = 50, required = true, value = "Consignee")
        var consignee: LabelParticipant = LabelParticipant()
        @ApiModelProperty(position = 60, required = true, value = "Appointment")
        var appointment: OrderService.Order.Appointment = OrderService.Order.Appointment()
        @ApiModelProperty(position = 70, required = false, value = "Total parcel amount", example = "2")
        @DefaultValue("1")
        var parcelAmount: Int? = null
        @ApiModelProperty(position = 80, required = false, value = "Parcel position", example = "2")
        @DefaultValue("1")
        var parcelPosition: Int? = null
        @ApiModelProperty(position = 90, required = true, value = "Parcel weight", example = "5.50")
        @DefaultValue("1.0")
        var weight: Double? = null
        @ApiModelProperty(position = 100, required = false, value = "Services", example = "[Postbox allowed, Tel. Empfangsbestaetigung]")
        var services: List<String>? = null
    }

    @POST
    @Path("/label")
    @ApiOperation(value = "[WORK IN PROGRESS] Get Label-Document")
    @Produces(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM)
    fun printParcelLabel(
            @ApiParam(value = "Label request", required = false) labelRequest: LabelRequest? = null,
            @QueryParam(PARCELID) @ApiParam(value = "Parcel ID", required = false) parcelId: Long? = null,      //TODO To be removed as this can be called via ParcelService??
            @QueryParam(PARCELNO) @ApiParam(value = "Parcel Number", required = false) parcelNo: String? = null, //TODO To be removed as this can be done via ParcelService??
            @QueryParam(RETURN_TYPE) @ApiParam(value = "Return Type", required = true, defaultValue = BASE64, allowableValues = "$BASE64, $PDF") returnType: String? = null
    ): Response
}