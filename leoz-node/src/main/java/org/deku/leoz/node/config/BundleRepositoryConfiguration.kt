package org.deku.leoz.node.config

import org.deku.leoz.bundle.BundleRepository
import sx.rsync.Rsync

/**
 * Created by n3 on 10-Nov-15.
 */
object BundleRepositoryConfiguration : org.deku.leoz.config.BundleRepositoryConfiguration() {
    val localRepository: BundleRepository by lazy({
        BundleRepository(
                Rsync.URI(StorageConfiguration.instance.get().bundleRepositoryDirectory))
    })
}