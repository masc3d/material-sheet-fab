package org.deku.leoz.ui.config

import com.github.salomonbrys.kodein.*
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.service.internal.update.BundleUpdateService

/**
 * Created by n3 on 11/24/16.
 */
class BundleUpdateConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<BundleUpdateService>() with eagerSingleton {
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
                                        bundleName = BundleType.LEOZ_UI.value,
                                        install = true,
                                        requiresBoot = true)))

                val connectionConfiguration =instance<ConnectionConfiguration>()

                connectionConfiguration.nodeUpdatedEvent.subscribe {
                    if (it.node != null) {
                        service.enabled = true
                        service.trigger()
                    }
                }

                service.enabled = false
                service.start()
                service
            }
        }
    }
}