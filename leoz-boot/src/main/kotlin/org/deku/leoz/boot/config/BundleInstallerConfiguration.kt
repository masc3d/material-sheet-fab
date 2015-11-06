package org.deku.leoz.boot.config

import org.deku.leoz.bundle.BundleInstaller

/**
 * Created by masc on 26-Oct-15.
 */
object BundleInstallerConfiguration {
    fun installer(): BundleInstaller {
        return BundleInstaller(
                StorageConfiguration.bundlesDirectory)
    }
}