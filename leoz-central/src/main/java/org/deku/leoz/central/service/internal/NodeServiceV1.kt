package org.deku.leoz.central.service.internal

import org.deku.leoz.service.internal.NodeServiceV1
import org.slf4j.LoggerFactory
import sx.mq.MqChannel
import sx.mq.MqHandler
import javax.inject.Named
import javax.ws.rs.*

/**
 * Created by masc on 17.02.16.
 */
@Named
@Path("internal/v1/node")
class NodeServiceV1
    :
        org.deku.leoz.service.internal.NodeServiceV1,
        MqHandler<NodeServiceV1.MobileStatus> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun onMessage(message: NodeServiceV1.MobileStatus, replyChannel: MqChannel?) {
        log.trace("${message}")

        // TODO handle status message
    }
}
