package org.deku.leoz.ui.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.github.salomonbrys.kodein.singleton
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
                val storageConfig: StorageConfiguration = instance()
                BundleInstaller(storageConfig.bundleInstallationDirectory)
            }

            bind<BundleRepository>() with provider {
                val config: BundleConfiguration = instance()
                createRepository(config.rsyncHost)
            }

            bind<BundleUpdateService>() with singleton {
                val config: BundleConfiguration = instance()
                val service = BundleUpdateService(
                        executorService = instance(),
                        bundleService = { instance() },
                        installer = instance(),
                        remoteRepository = { instance() },
                        presets = listOf(
                                BundleUpdateService.Preset(
                                        bundleName = BundleType.LEOZ_BOOT.value,
                                        install = true),
                                BundleUpdateService.Preset(
                                        bundleName = BundleType.LEOZ_UI.value)))
                service
            }
        }
    }

    /** Remote repository rsync host */
    var rsyncHost: String = "localhost"
}