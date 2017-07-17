package org.deku.leoz.central.service.internal

import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.rs.auth.ApiKey
import javax.inject.Named
import javax.ws.rs.Path

/**
 * Created by JT on 17.07.17.
 */
class EventService {
}

@Named
@ApiKey(true)
@Path("internal/v1/event")
open class EventServiceV1
    : MqHandler<EventServiceV1> {
    override fun onMessage(message: EventServiceV1, replyChannel: MqChannel?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.


        // save to mysql


    }
}
