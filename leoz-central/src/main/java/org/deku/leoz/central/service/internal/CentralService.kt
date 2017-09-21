package org.deku.leoz.central.service.internal

import org.deku.leoz.central.service.internal.sync.DatabaseSyncService
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.Produces

/**
 * Created by JT on 05.02.16.
 */
@Named
@Path("internal/v1/central")
@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
class CentralService : org.deku.leoz.service.internal.CentralService {
    private val log = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    @Inject
    lateinit var databaseSyncService: DatabaseSyncService

    override fun sync(clean: Boolean) {
        this.databaseSyncService.startSync(clean = clean)
    }
}
