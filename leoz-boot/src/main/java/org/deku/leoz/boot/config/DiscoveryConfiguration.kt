package org.deku.leoz.boot.config

import com.github.salomonbrys.kodein.*
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.service.internal.DiscoveryService

/**
 * Discovery configuration
 * Created by masc on 07/11/2016.
 */
class DiscoveryConfiguration {
    companion object {
        val module = Kodein.Module {
            /** Discovery service */
            bind<DiscoveryService>() with eagerSingleton {
                val service = DiscoveryService(
                        executorService = instance(),
                        bundleType = BundleType.LEOZ_BOOT,
                        passive = true
                )
                service.start()
                service
            }
        }
    }
}