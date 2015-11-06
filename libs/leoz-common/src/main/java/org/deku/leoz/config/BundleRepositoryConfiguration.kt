package org.deku.leoz.config

import org.deku.leoz.bundle.BundleRepository
import sx.rsync.Rsync

/**
 * Created by masc on 22.08.15.
 */
object BundleRepositoryConfiguration {
    private var stagingRsyncModuleUri = Rsync.URI("rsync://leoz@syntronix.de/leoz")
    private var stagingRsyncModulePassword = "leoz"

    fun stagingRepository(): BundleRepository {
        return BundleRepository(
                rsyncModuleUri = stagingRsyncModuleUri,
                rsyncPassword = stagingRsyncModulePassword)
    }
}