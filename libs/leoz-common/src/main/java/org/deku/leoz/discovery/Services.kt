package org.deku.leoz.discovery

import org.deku.leoz.bundle.Bundles

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
 * Leoz service info, used for providing exposed services
 * Created by masc on 22/08/16.
 */
open class ServiceInfo(
        val serviceType: ServiceType,
        val port: Int) {
    override fun toString(): String {
        return "ServiceInfo(serviceType=$serviceType, port=$port)"
    }
}


/**
 * Leoz discovered service info, used for notifying about discovered services
 * Created by masc on 29/08/16.
 */
class DiscoveredServiceInfo(serviceType: ServiceType,
                            port: Int,
                            val host: String,
                            val bundleType: Bundles) : ServiceInfo(serviceType, port) {
    override fun toString(): String {
        return "DiscoveredServiceInfo(serviceType=$serviceType, port=$port, host='$host', bundleType=$bundleType)"
    }
}