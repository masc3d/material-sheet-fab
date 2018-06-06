package org.deku.leoz.central.service.internal

import org.deku.leoz.central.Application
import org.deku.leoz.central.service.internal.sync.DatabaseSyncService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import javax.inject.Inject
import javax.ws.rs.Path

@Component
@Path("internal/v1/application")
@Profile(Application.PROFILE_CENTRAL)
class ApplicationService : org.deku.leoz.node.service.internal.ApplicationService() {

    @Inject
    private lateinit var dbSyncService: DatabaseSyncService

    override fun syncWithCentralDatabase(clean: Boolean) {
        this.dbSyncService.sync(clean = clean)
    }
}