package org.deku.leoz.discovery

import org.deku.leoz.bundle.BundleType
import org.deku.leoz.config.DiscoveryConfiguration
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.logging.slf4j.info

/**
 * Created by masc on 22/08/16.
 */
class BroadcastDiscoveryServiceTest {
    val log = LoggerFactory.getLogger(this.javaClass)

    @Ignore
    @Test
    fun testWithExposedServiceInfos() {
        val ds = org.deku.leoz.discovery.impl.BroadcastDiscoveryService(
                port = DiscoveryConfiguration.port,
                bundleType = BundleType.LEOZ_NODE,
                serviceInfos = arrayListOf(
                        ServiceInfo(serviceType = ServiceType.HTTPS, port = 13000),
                        ServiceInfo(ServiceType.ACTIVEMQ_NATIVE, 13001),
                        ServiceInfo(ServiceType.RSYNC, 13002),
                        ServiceInfo(ServiceType.SSH, 13003)))

        ds.start()
        Thread.sleep(Long.MAX_VALUE)
        ds.stop()
    }

    @Ignore
    @Test
    fun testWithoutExposedServiceInfos() {
        val ds = org.deku.leoz.discovery.impl.BroadcastDiscoveryService(
                port = DiscoveryConfiguration.port,
                bundleType = BundleType.LEOZ_NODE)

        ds.start()
        Thread.sleep(Long.MAX_VALUE)
        ds.stop()
    }
}