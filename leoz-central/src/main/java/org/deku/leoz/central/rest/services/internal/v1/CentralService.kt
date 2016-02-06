package org.deku.leoz.central.rest.services.internal.v1

import org.apache.commons.logging.LogFactory
import org.deku.leoz.central.config.DatabaseSyncConfiguration
import org.deku.leoz.central.data.sync.DatabaseSync
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
class CentralService : org.deku.leoz.rest.services.internal.v1.CentralService {
    private val log = LogFactory.getLog(this.javaClass)

    @Inject
    lateinit var databaseSync: DatabaseSync

//    @Inject
//    lateinit var databaseSyncConfiguration: DatabaseSyncConfiguration

    override fun databaseSync() {
        log.info("Manually database sync")

//        this.databaseSyncConfiguration.trigger()
        this.databaseSync.sync()
    }
}
