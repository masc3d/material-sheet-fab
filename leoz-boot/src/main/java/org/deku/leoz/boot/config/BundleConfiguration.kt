package org.deku.leoz.boot.config

import com.github.salomonbrys.kodein.*
import org.deku.leoz.Storage
import sx.packager.BundleInstaller
import sx.packager.BundleRepository
import org.deku.leoz.config.HostConfiguration

/**
 * Created by masc on 16/11/2016.
 */
class BundleConfiguration : org.deku.leoz.config.BundleConfiguration() {
    companion object {
        val module = Kodein.Module {
            bind<BundleConfiguration>() with singleton {
                BundleConfiguration()
            }
            bind<BundleInstaller>() with singleton {
                val storage: Storage = instance()
                BundleInstaller(storage.bundleInstallationDirectory)
            }
            bind<BundleRepository>() with provider {
                val config: BundleConfiguration = instance()
                createRepository(config.rsyncHost)
            }
        }
    }

    var rsyncHost: String = HostConfiguration.CENTRAL_HOST
}