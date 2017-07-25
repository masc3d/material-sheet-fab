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

            val evtReasonDeliveredNormal: EvtReasonDeliveredNormal? = null,
            val evtReasonDeliveredNeighbor: EvtReasonDeliveredNeighbor? = null,
            val evtReasonNotDeliveredAbsent: EvtReasonNotDeliveredAbsent? = null,
            val evtReasonNotDeliveredRefuse: EvtReasonNotDeliveredRefuse? = null,
            val evtReasonNotDeliveredWrongAddress: EvtReasonNotDeliveredWrongAddress? = null
    )

    data class EvtReasonDeliveredNormal(
            val event: Int = 1,
            val eventReason: Int = 1,
            val nameRecipient: String? = null,
            val signatureDelivery: String? = null
    )

    data class EvtReasonDeliveredNeighbor(
            val event: Int = 1,
            val eventReason: Int = 2,
            val nameNeighbor: String? = null,
            val addressNeighbor: String? = null,
            val signatureNeighbor: String? = null
    )

    data class EvtReasonNotDeliveredAbsent(
            val event: Int = 2,
            val eventReason: Int = 500
    )

    data class EvtReasonNotDeliveredRefuse(
            val event: Int = 2,
            val eventReason: Int = 503,
            val inputWhoWhy: String? = null
    )

    data class EvtReasonNotDeliveredWrongAddress(
            val event: Int = 2,
            val eventReason: Int = 506

    )
}

