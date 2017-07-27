package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import org.deku.leoz.model.AdditionalInfo
import sx.io.serialization.Serializable
import java.util.*
import javax.ws.rs.Consumes
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Created by JT on 17.07.17.
 */
@Path("internal/v1/parcel")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Parcel service")
interface ParcelServiceV1 {
    companion object {
        const val EVENT = 1
    }

    /**
     * Event message sent by nodes/devices
     */
    @Serializable(0x25cddba04cdfe0)
    data class ParcelMessage(
            var userId: Int? = null,
            var nodeId: String? = null,
            var events: Array<Event>? = null
    )

    /**
     * A Event used in leoz prototols
     */
    @Serializable(0x34fd1e3a4992bb)
    data class Event(
            val event: Int = 0,
            val reason: Int = 0,
            val parcelId: Int = 0,
            val parcelScancode: String = "",//possibly alphanumeric
            val time: Date = Date(),
            val latitude: Double? = null,
            val longitude: Double? = null,
            //proposal/experimental for other events in future...
            val fromStation: Boolean = true, //maybe there will be a new app for line... and there are events for both e.g. damaged & photo
            val from: String,//lineNo or StationNo
            val additionalInfo: AdditionalInfo = AdditionalInfo.EmptyInfo
            /** proposal/experimental for other events in future...
            val deliveredInfo: DeliveredInfo? = null,
            val deliveredAtNeighborInfo: DeliveredAtNeighborInfo? = null,
            val notDeliveredRefusedInfo: NotDeliveredRefusedInfo? = null
             **/
    )


}

