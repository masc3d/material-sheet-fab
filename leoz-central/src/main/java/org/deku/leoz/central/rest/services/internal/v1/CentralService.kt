package org.deku.leoz.central.rest.services.internal.v1

import org.deku.leoz.central.service.DatabaseSyncService
import org.slf4j.LoggerFactory
import sx.rs.ApiKey
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Created by JT on 05.02.16.
 */
@Named
@ApiKey(false)
@Path("internal/v1/central")
@Produces(MediaType.APPLICATION_JSON)
class CentralService : org.deku.leoz.rest.service.internal.v1.CentralService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    lateinit var databaseSyncService: DatabaseSyncService

    override fun databaseSync() {
        this.databaseSyncService.trigger()
    }
}
