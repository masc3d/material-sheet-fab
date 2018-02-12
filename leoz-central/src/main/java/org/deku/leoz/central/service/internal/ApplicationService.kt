package org.deku.leoz.central.service.internal

import org.deku.leoz.central.Application
import org.deku.leoz.central.service.internal.sync.DatabaseSyncService
import org.springframework.context.annotation.Profile
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path

@Named
@Path("internal/v1/application")
@Profile(Application.PROFILE_CENTRAL)
class ApplicationService : org.deku.leoz.node.service.internal.ApplicationService() {

    @Inject
    private lateinit var mDatabaseSyncService: DatabaseSyncService

    override fun syncWithCentralDatabase(clean: Boolean) {
        this.mDatabaseSyncService.startSync(clean = clean)
    }
}