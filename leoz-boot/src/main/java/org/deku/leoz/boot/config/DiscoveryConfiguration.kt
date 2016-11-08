package org.deku.leoz.boot.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.service.discovery.DiscoveryService

/**
 * Discovery configuration
 * Created by masc on 07/11/2016.
 */
object DiscoveryConfiguration {
    val module = Kodein.Module {
        /** Discovery service */
        bind<DiscoveryService>() with singleton {
            DiscoveryService(
                    executorService = instance(),
                    bundleType = BundleType.LEOZ_BOOT,
                    serverEnabled = false
            )
        }
    }
}