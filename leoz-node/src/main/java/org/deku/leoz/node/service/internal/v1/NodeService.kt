package org.deku.leoz.node.service.internal.v1

import org.deku.leoz.node.config.EntitySyncConfiguration
import org.deku.leoz.service.internal.v1.NodeService
import org.slf4j.LoggerFactory
import sx.rs.auth.ApiKey
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path

/**
 * Created by masc on 17.02.16.
 */
@Named
@ApiKey(false)
@Path("internal/v1/node")
class NodeService : NodeService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    lateinit var entitySyncConfiguration: EntitySyncConfiguration

    override fun sync(clean: Boolean) {
        this.entitySyncConfiguration.requestEntities(clean = clean)
    }
}
