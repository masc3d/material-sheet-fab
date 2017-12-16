package org.deku.leoz.central.service.internal

import org.deku.leoz.service.internal.TourServiceV1
import org.slf4j.LoggerFactory
import sx.mq.MqChannel
import sx.mq.MqHandler
import javax.inject.Named
import javax.ws.rs.Path

/**
 * Tour service implementation
 * Created by masc on 14.12.17.
 */
@Named
@Path("internal/v1/tour")
class TourServiceV1
    :
        org.deku.leoz.service.internal.TourServiceV1,
        MqHandler<TourServiceV1.TourUpdateMessage>
{
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun get(id: Int): TourServiceV1.Tour {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(nodeUid: String, userId: Int): TourServiceV1.Tour {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMessage(message: TourServiceV1.TourUpdateMessage, replyChannel: MqChannel?) {
        log.trace("Tour message ${message}")
    }
}