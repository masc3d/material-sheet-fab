package org.deku.leoz.node.rest.services.internal.v1

import org.apache.commons.logging.LogFactory
import org.deku.leoz.node.config.EntitySyncConfiguration
import org.deku.leoz.rest.services.internal.v1.NodeService
import sx.rs.ApiKey
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Created by masc on 17.02.16.
 */
@Named
@ApiKey(false)
@Path("internal/v1/node")
@Produces(MediaType.APPLICATION_JSON)
class NodeService : org.deku.leoz.rest.services.internal.v1.NodeService {
    private val log = LogFactory.getLog(this.javaClass)

    @Inject
    lateinit var entitySyncConfiguration: EntitySyncConfiguration

    override fun entitySync() {
        this.entitySyncConfiguration.requestEntities()
    }
}
