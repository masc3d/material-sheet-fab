package org.deku.leoz.node.config

import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.config.BundleConfiguration

/**
 * Created by masc on 09/03/16.
 */
object BundleConfiguration : BundleConfiguration() {
    /**
     * Application wide bundle installer
     */
    fun bundleInstaller(): BundleInstaller {
        return BundleInstaller(
                StorageConfiguration.instance.bundleInstallationDirectory)
    }
}