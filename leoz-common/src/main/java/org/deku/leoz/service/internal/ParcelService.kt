package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import sx.io.serialization.Serializable
import java.util.*
import javax.ws.rs.Consumes
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Created by JT on 17.07.17.
 */
@Path("internal/v1/event")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Event service")
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
            var dataPoints: Array<Event>? = null
    )

    /**
     * A Event used in leoz prototols
     */
    @Serializable(0x34fd1e3a4992bb)
    data class Event(
            val parcelId: Int = 0,
            val parcelScan: String = "",
            val time: Date = Date(),
            val latitude: Double? = null,
            val longitude: Double? = null,

            val evtResDeliverdNormal: EvtResDeliverdNormal? = null,
            val evtResDeliverdNeighbor: EvtResDeliverdNeighbor? = null,
            val evtResNotDeliverdAbsend: EvtResNotDeliverdAbsend? = null,
            val evtResNotDeliverdRefuse: EvtResNotDeliverdRefuse? = null
    )

    data class EvtResDeliverdNormal(
            val event: Int = 1,
            val evventReason: Int = 1,
            val nameRecipient: String? = null,
            val signatureDelivery: String? = null
    )

    data class EvtResDeliverdNeighbor(
            val event: Int = 1,
            val evventReason: Int = 2,
            val nameNeighbor: String? = null,
            val addressNeighbor: String? = null,
            val signatureNeighbor: String? = null
    )

    data class EvtResNotDeliverdAbsend(
            val event: Int = 2,
            val evventReason: Int = 500
    )

    data class EvtResNotDeliverdRefuse(
            val event: Int = 2,
            val evventReason: Int = 503,
            val inputWhoWhy: String? = null
    )
}

