package org.deku.leoz.node.config

import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.config.BundleConfiguration
import sx.rsync.Rsync

/**
 * Created by masc on 09/03/16.
 */
object BundleConfiguration : BundleConfiguration() {
    val localRepository: BundleRepository by lazy {
        BundleRepository(
                rsyncModuleUri = Rsync.URI(StorageConfiguration.instance.bundleRepositoryDirectory))
    }

    /**
     * Application wide bundle installer
     */
    fun bundleInstaller(): BundleInstaller {
        return BundleInstaller(
                StorageConfiguration.instance.bundleInstallationDirectory)
    }
}