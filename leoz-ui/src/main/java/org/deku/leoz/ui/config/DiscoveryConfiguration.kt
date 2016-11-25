package org.deku.leoz.ui.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.service.discovery.DiscoveryService

/**
 * Discovery configuration
 * Created by masc on 22/11/2016.
 */
class DiscoveryConfiguration {
    companion object {
        val module = Kodein.Module {
            /** Discovery service */
            bind<DiscoveryService>() with singleton {
                val service = DiscoveryService(
                        executorService = instance(),
                        bundleType = BundleType.LEOZ_UI,
                        passive = true)
                service.start()
                service
            }
        }
    }
}