package org.deku.leoz.config

import org.deku.leoz.bundle.Bundles
import org.deku.leoz.discovery.DiscoveryService
import org.deku.leoz.discovery.ServiceInfo

/**
 * Created by masc on 22/08/16.
 */
object DiscoveryConfiguration {
    /** Discovery port must be static */
    const val port = 13004

    fun createDiscoveryService(bundleType: Bundles, vararg serviceInfos: ServiceInfo): DiscoveryService {
        return DiscoveryService(
                port = this.port,
                bundleType =  bundleType,
                serviceInfos = *serviceInfos)
    }
}