package org.deku.leoz.node.rest.service.internal.v1

import io.swagger.annotations.ApiParam
import org.deku.leoz.node.config.EntitySyncConfiguration
import org.slf4j.LoggerFactory
import sx.rs.ApiKey
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.QueryParam

/**
 * Created by masc on 17.02.16.
 */
@Named
@ApiKey(false)
@Path("internal/v1/node")
class NodeService : org.deku.leoz.rest.service.internal.v1.NodeService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    lateinit var entitySyncConfiguration: EntitySyncConfiguration

    override fun sync(clean: Boolean) {
        this.entitySyncConfiguration.requestEntities(clean = clean)
    }
}
