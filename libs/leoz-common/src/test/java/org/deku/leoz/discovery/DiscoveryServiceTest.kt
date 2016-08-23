package org.deku.leoz.discovery

import org.deku.leoz.bundle.Bundles
import org.deku.leoz.config.DiscoveryConfiguration
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Created by masc on 22/08/16.
 */
class DiscoveryServiceTest {
    val discoveryService by lazy {
        DiscoveryService(
                port = DiscoveryConfiguration.port,
                bundleType = Bundles.LEOZ_NODE,
                serviceInfos = arrayListOf(
                        ServiceInfo(ServiceType.HTTPS, 13000),
                        ServiceInfo(ServiceType.ACTIVEMQ_NATIVE, 13001),
                        ServiceInfo(ServiceType.RSYNC, 13002),
                        ServiceInfo(ServiceType.SSH, 13003)))
    }
    @Test
    fun testDiscoveryService() {
        this.discoveryService.start()
        Thread.sleep(Long.MAX_VALUE)
        this.discoveryService.stop()
    }
}