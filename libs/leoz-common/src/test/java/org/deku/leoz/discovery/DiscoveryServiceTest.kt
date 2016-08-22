package org.deku.leoz.discovery

import org.deku.leoz.bundle.Bundles
import org.deku.leoz.config.DiscoveryConfiguration
import org.junit.Test

/**
 * Created by masc on 22/08/16.
 */
class DiscoveryServiceTest {
    val discoveryService by lazy {
        DiscoveryService(
                port = DiscoveryConfiguration.port,
                bundleType = Bundles.LEOZ_NODE,
                serviceInfos = ServiceInfo(ServiceType.HTTPS, 13000))
    }
    @Test
    fun testDiscoveryService() {
        this.discoveryService.start()
        this.discoveryService.stop()
    }
}