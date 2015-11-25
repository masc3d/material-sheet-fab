package org.deku.leoz.config

import org.deku.leoz.bundle.BundleRepository
import sx.rsync.Rsync

/**
 * Created by masc on 22.08.15.
 */
abstract class BundleRepositoryConfiguration {
    val stagingRepository: BundleRepository by lazy({
        BundleRepository(
                rsyncModuleUri = Rsync.URI("rsync://leoz@syntronix.de/leoz"),
                rsyncPassword = "leoz")
    })


}