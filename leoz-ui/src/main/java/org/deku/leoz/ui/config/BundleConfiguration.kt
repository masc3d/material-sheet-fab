package org.deku.leoz.ui.config

import com.github.salomonbrys.kodein.*
import org.deku.leoz.Storage
import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.service.update.BundleUpdateService

/**
 * Bundle configuration
 * Created by masc on 22/11/2016.
 */
class BundleConfiguration : org.deku.leoz.config.BundleConfiguration() {
    companion object {
        val module = Kodein.Module {
            bind<BundleConfiguration>() with singleton {
                BundleConfiguration()
            }

            bind<BundleInstaller>() with singleton {
                val storageConfig: Storage = instance()
                BundleInstaller(storageConfig.bundleInstallationDirectory)
            }

            bind<BundleRepository>() with provider {
                val config: BundleConfiguration = instance()
                createRepository(config.rsyncHost)
            }
        }
    }

    /** Remote repository rsync host */
    var rsyncHost: String = "localhost"
}