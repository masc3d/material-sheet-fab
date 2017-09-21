package org.deku.leoz.node.service.internal

import org.deku.leoz.node.config.EntitySyncConfiguration
import org.slf4j.LoggerFactory
import sx.rs.auth.ApiKey
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.*

/**
 * Created by masc on 17.02.16.
 */
@Named
@Path("internal/v1/node")
class NodeService : org.deku.leoz.service.internal.NodeService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    lateinit var entitySyncConfiguration: EntitySyncConfiguration

    override fun sync(clean: Boolean) {
        this.entitySyncConfiguration.requestEntities(clean = clean)
    }
}
