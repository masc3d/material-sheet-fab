package org.deku.leoz.config

import org.deku.leoz.bundle.BundleType
import org.deku.leoz.discovery.ServiceInfo

/**
 * Created by masc on 22/08/16.
 */
object DiscoveryConfiguration {
    /** Discovery port must be static */
    const val port = 13004

    fun createDiscoveryService(bundleType: BundleType, serviceInfos: List<ServiceInfo>): org.deku.leoz.discovery.impl.JmDNSDiscoveryService {
        return org.deku.leoz.discovery.impl.JmDNSDiscoveryService(
                port = this.port,
                bundleType = bundleType,
                serviceInfos = serviceInfos)
    }
}