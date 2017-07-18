package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import org.deku.leoz.model.VehicleType
import sx.io.serialization.Serializable
import sx.mq.MqHandler
import sx.rs.auth.ApiKey
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
            val event: Int = 0,
            val reason: Int = 0,
            val time: Date = Date(),

            // todo ja! da muss noch was getan werden. ist in disscuss
            val note: String? = null,
            // recipient- / consigner- /etc. name in context of event_reason
            //

            val xxx: xxx? = null,
            val latitude: Double? = null,
            val longitude: Double? = null
    )

    //todo spezial eventreasonvalues to return
    @Serializable(0x884f7f04a26346)
    data class xxx(
            val xxx: String? = null
    )


}

