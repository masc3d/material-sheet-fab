package org.deku.leoz.node.service.internal

import org.slf4j.LoggerFactory
import javax.inject.Named
import javax.ws.rs.*

/**
 * Created by masc on 17.02.16.
 */
@Named
@Path("internal/v1/node")
class NodeService : org.deku.leoz.service.internal.NodeService {
    private val log = LoggerFactory.getLogger(this.javaClass)

}
