package org.deku.leoz.bundle

import sx.rsync.Rsync
import sx.rsync.RsyncClient

/**
 * Created by masc on 22.08.15.
 */
object BundleRepositoryFactory {
    private var stagingRsyncModuleUri = Rsync.URI("rsync://leoz@syntronix.de/leoz")
    private var stagingRsyncModulePassword = "leoz"

    fun stagingRepository(bundleName: String): BundleRepository {
        return BundleRepository(
                bundleName = bundleName,
                rsyncModuleUri = stagingRsyncModuleUri,
                rsyncPassword = stagingRsyncModulePassword)
    }
}