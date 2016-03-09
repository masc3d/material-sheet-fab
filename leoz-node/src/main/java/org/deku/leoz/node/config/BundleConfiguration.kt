package org.deku.leoz.node.config

import org.deku.leoz.bundle.BundleInstaller

/**
 * Created by masc on 09/03/16.
 */
object BundleConfiguration {
    /**
     * Application wide bundle installer
     */
    fun bundleInstaller(): BundleInstaller {
        return BundleInstaller(
                StorageConfiguration.instance.bundleInstallationDirectory)
    }
}