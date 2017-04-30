package org.deku.leoz.central.service.internal

import org.deku.leoz.central.service.internal.sync.DatabaseSyncService

/**
 * Created by JT on 05.02.16.
 */
@javax.inject.Named
@sx.rs.auth.ApiKey(false)
@javax.ws.rs.Path("internal/v1/central")
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
class CentralService : org.deku.leoz.service.internal.CentralService {
    private val log = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    @javax.inject.Inject
    lateinit var databaseSyncService: DatabaseSyncService

    override fun sync(clean: Boolean) {
        this.databaseSyncService.startSync(clean = clean)
    }
}
