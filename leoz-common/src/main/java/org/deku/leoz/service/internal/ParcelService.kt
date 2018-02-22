package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.Authorization
import org.deku.leoz.config.Rest
import org.deku.leoz.model.AdditionalInfo
import sx.io.serialization.Serializable
import sx.rs.auth.ApiKey
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Parcel service
 * Created by JT on 17.07.17.
 */
@Path("internal/v1/parcel")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Parcel service")
@ApiKey(false)
interface ParcelServiceV1 {
    companion object {
        const val EVENT = 1
        const val STATION_NO = "station-no"
        const val LOADINGLIST_NO = "loadinglist-no"
        const val SCANCODE = "parcel-no"
        const val PARCEL_ID = "parcel-id"
        const val PARCEL_REF = "parcel-ref"
        const val PARCEL_WEB_REF = "parcel-web-ref"
        const val BAG_ID = "bag-id"
        const val CONSIGNEE_ZIPCODE = "consignee-zipcode"
    }

    /**
     * Event message sent by nodes/devices
     */
    @Serializable(0x25cddba04cdfe0)
    data class ParcelMessage(
            var userId: Int? = null,
            var nodeId: String? = null,
            var events: Array<Event>? = null,

            val deliveredInfo: DeliveredInfo? = null,
            val signatureOnPaperInfo: SignatureOnPaperInfo? = null,
            val postboxDeliveryInfo: PostboxDeliveryInfo? = null
    ) {
        data class DeliveredInfo(
                val recipient: String? = null,
                val signature: String? = null,
                val mimetype: String = MediaType.APPLICATION_SVG_XML //? "image/svg+xml" vs "application/svg+xml"? "image/jpg"
        )

        data class SignatureOnPaperInfo(
                val recipient: String? = null,
                val pictureFileUid: UUID? = null
        )

        data class PostboxDeliveryInfo(
                val pictureFileUid: UUID? = null
        )
    }

    /**
     * A Event used in leoz prototols
     */
    @Serializable(0x34fd1e3a4992bb)
    data class Event(
            val event: Int = 0,
            val reason: Int = 0,
            val parcelId: Long = 0,

            val time: Date = Date(),
            val latitude: Double? = null,
            val longitude: Double? = null,

            //proposal/experimental for other events in future...
            val fromStation: Boolean = true, //maybe there will be a new app for line... and there are events for both e.g. damaged & photo
            val from: String? = null, //lineNo or StationNo

            // TODO: no object graphs with derivation. make it plain (see below). marked as @Transient as kryo will choke on this.
            @Transient
            val additionalInfo: AdditionalInfo = AdditionalInfo.EmptyInfo,

            val damagedInfo: DamagedInfo? = null
    ) {
        data class DamagedInfo(
                /** Array of picture file uids */
                val pictureFileUids: Array<UUID> = arrayOf()
        )
    }


    @Serializable(0xd035c452897ee3)
    data class ParcelStatus(
            var parcelNo: Long = 0,
            var creator: String = "",
            var status: Int = 0,
            var errorcode: Int = 0,
            var note: String = ""
    )

    // Proposal
    data class ParcelWebStatus(
            var status: List<ParcelStatus> = listOf(),
            var consignee: String? = null,
            var signatory: String? = null
    )

    // Proposal
    @GET
    @Path("/")
    @ApiOperation(value = "Find parcel", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun findParcel(
            @QueryParam(value = SCANCODE) @ApiParam("Scancode") scanCode: String,
            @QueryParam(value = PARCEL_REF) @ApiParam("Reference") reference: String
    ): List<OrderService.Order.Parcel>

    // Proposal
    @GET
    @Path("/webstatus")
    @ApiOperation(value = "Get web status (public)")
    fun getWebStatus(
            @PathParam(PARCEL_WEB_REF) @ApiParam(value = "Search reference (eg. parcel-no, customer-reference)", required = true) searchRef: String,
            @PathParam(CONSIGNEE_ZIPCODE) @ApiParam(value = "Zip code of consignee. If given, more information are provided.", required = false) zipCode: String?
    ): List<ParcelStatus>

    @GET
    @Path("/{$PARCEL_ID}/status")
    @ApiOperation(value = "Get status", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getStatus(
            @PathParam(PARCEL_ID) @ApiParam(value = "Unique Parcel Identifier") scanCode: String
    ): List<ParcelStatus>

    @GET
    @Path("/{$PARCEL_ID}/label")
    @ApiOperation(value = "Get label", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getLabel(
            @PathParam(PARCEL_ID) @ApiParam(value = "Unique Parcel Identifier") parcelId: Long
    ): Response
}

