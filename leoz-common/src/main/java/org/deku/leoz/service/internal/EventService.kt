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
interface EventServiceV1 {


    /**
     * Event message sent by nodes/devices
     */
    @Serializable(0xd307ea744273ae)
    data class EventMessage(
            var userId: Int? = null,
            var nodeId: String? = null,
            var dataPoints: Array<EventServiceV1.Event>? = null
    )


    /**
     * A Event used in leoz prototols
     */
    @Serializable(0x5af819e313304e)
    data class Event(
            val event: Double? = null,
            val reason: Double? = null,
            val time: Date? = null,
            val note: String? = null,
            val xxx: xxx? = null,
            val latitude: Double? = null,
            val longitude: Double? = null
    )

    //todo spezial eventvalues to return
    @Serializable(0x999)
    data class xxx(
            val xxx: String? = null
    )


}

