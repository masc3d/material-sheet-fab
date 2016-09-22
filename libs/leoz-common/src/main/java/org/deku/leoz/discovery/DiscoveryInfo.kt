package org.deku.leoz.discovery

import org.deku.leoz.bundle.BundleType
import sx.Copyable
import sx.io.serialization.Serializable

/**
 * Created by masc on 22/09/2016.
 */
@Serializable(0x103d252eea28df)
data class DiscoveryInfo(
        val bundleType: BundleType? = null,
        val services: Array<Service> = arrayOf()) {
    /**
     * Leoz service types
     * Created by masc on 22/08/16.
     */
    enum class ServiceType(val value: String) {
        HTTPS("https"),
        ACTIVEMQ_NATIVE("activemq_native"),
        RSYNC("rsync"),
        SSH("ssh")
    }

    /**
     * Leoz discovered service info, used for notifying about discovered services
     * Created by masc on 29/08/16.
     */
    data class Service(val serviceType: ServiceType,
                       val port: Int,
                       val host: String)
}