package org.deku.leoz.node.rest.services.internal.v1

import org.deku.leoz.node.config.EntitySyncConfiguration
import org.slf4j.LoggerFactory
import sx.rs.ApiKey
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path

/**
 * Created by masc on 17.02.16.
 */
@Named
@ApiKey(false)
@Path("internal/v1/node")
class NodeService : org.deku.leoz.rest.services.internal.v1.NodeService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    lateinit var entitySyncConfiguration: EntitySyncConfiguration

    override fun entitySync() {
        this.entitySyncConfiguration.requestEntities()
    }
}
