package org.deku.leoz.service.internal

import io.swagger.annotations.*
import org.deku.leoz.model.AdditionalInfo
import org.deku.leoz.service.internal.entity.Address
import sx.io.serialization.Serializable
import sx.rs.auth.ApiKey
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

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
        const val PARCEL_NO = "parcel-no"
        const val CREFERENZ = "cReferenz"
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

            // TODO: remove. parcelId is sufficient
            val parcelScancode: String = "", //possibly alphanumeric

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

    @GET
    @Path("/export/{$STATION_NO}")
    @ApiOperation(value = "Get parcels to export")
    fun getParcels2ExportByStationNo(
            @PathParam(STATION_NO) @ApiParam(value = "station number", example = "220", required = true) stationNo: Int
    ): List<ParcelServiceV1.Order2Export>

    @GET
    @Path("/export/loaded/{$STATION_NO}")
    @ApiOperation(value = "Get loaded parcels to export")
    fun getLoadedParcels2ExportByStationNo(
            @PathParam(STATION_NO) @ApiParam(value = "station number", example = "220", required = true) stationNo: Int
    ): List<ParcelServiceV1.Order2Export>

    @GET
    @Path("/loadinglist/new")
    @ApiOperation(value = "Get new loadinglist-no")
    fun getNewLoadinglistNo(): Long

    @GET
    @Path("/loadinglist/{$LOADINGLIST_NO}")
    @ApiOperation(value = "Get parcels by loadinglist")
    fun getParcels2ExportByLoadingList(
            @PathParam(LOADINGLIST_NO) @ApiParam(value = "loadinglist number", example = "300005", required = true) loadinglistNo: Long
    ): List<ParcelServiceV1.Order2Export>

    @PUT
    @Path("/export")
    @ApiOperation(value = "export parcel")
    fun export(
            @QueryParam(PARCEL_NO) @ApiParam(value = "parcel no") parcelNo: String? = null,
            @QueryParam(CREFERENZ) @ApiParam(value = "cReferenz") cReferenz: String? = null,
            @QueryParam(LOADINGLIST_NO) @ApiParam(value = "loadinglist no", required = true) loadingListNo: Long
    ): Boolean

    data class Order2Export(
            var orderId: Long = 0,
            var deliveryAddress: Address = Address(),
            var deliveryStation: Int = 0,
            var shipmentDate: java.sql.Date? = null,
            var parcels: List<Parcel2Export>? = null//= listOf()

    )

    data class Parcel2Export(
            var orderId: Long = 0,
            var parcelNo: Long = 0,
            var parcelPosition: Int = 0,
            var loadinglistNo: Long? = null,
            var typeOfPackaging: Int = 0,
            var realWeight: Double = 0.0,
            var dateOfStationOut: java.sql.Date? = null,
            var cReference: String? = null
    )
}

