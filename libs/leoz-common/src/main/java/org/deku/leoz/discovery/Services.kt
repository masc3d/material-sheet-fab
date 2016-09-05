package org.deku.leoz.discovery

import org.deku.leoz.bundle.BundleType
import sx.Copyable
import java.io.Serializable

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
data class ServiceInfo(val serviceType: ServiceType,
                       val port: Int,
                       val host: String,
                       val bundleType: BundleType) : Serializable, Copyable<ServiceInfo> {
    override fun copyInstance(): ServiceInfo {
        return this.copy()
    }

    override fun toString(): String {
        return "DiscoveredServiceInfo(serviceType=$serviceType, port=$port, host='$host', bundleType=$bundleType)"
    }
}