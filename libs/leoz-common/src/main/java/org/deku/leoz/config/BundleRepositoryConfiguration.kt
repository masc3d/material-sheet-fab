package org.deku.leoz.config

import org.deku.leoz.bundle.BundleRepository
import sx.rsync.Rsync

/**
 * Created by masc on 22.08.15.
 */
abstract class BundleRepositoryConfiguration {
    val stagingRepository: BundleRepository by lazy({
        BundleRepository(
                rsyncModuleUri = Rsync.URI("rsync://leoz@leoz.derkurier.de:13002/bundles"),
                rsyncPassword = RsyncConfiguration.PASSWORD,
                sshTunnelProvider = SshConfiguration.tunnelProvider)
    })
}