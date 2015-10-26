package org.deku.leoz.boot.config

import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.bundle.BundleRepositoryFactory

/**
 * Created by masc on 26-Oct-15.
 */
object BundleInstallerConfiguration {
    fun installerForBundle(bundleName: String): BundleInstaller {
        return BundleInstaller(
                StorageConfiguration.bundlesDirectory,
                bundleName,
                BundleRepositoryFactory.stagingRepository())
    }
}