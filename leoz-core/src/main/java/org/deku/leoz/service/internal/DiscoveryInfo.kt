package org.deku.leoz.service.internal

import org.deku.leoz.bundle.BundleType
import sx.io.serialization.Serializable
import java.util.*

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
        SSH("ssh"),
        HTTP("http")
    }

    /**
     * Leoz discovered service info, used for notifying about discovered services
     * Created by masc on 29/08/16.
     */
    data class Service(val type: ServiceType? = null,
                       val port: Int = 0)

    /**
     * Custom equality comparer (data classes don't do this properly with arrays)
     */
    override fun equals(other: Any?): Boolean {
        if (other == null)
            return false

        if (!(other is DiscoveryInfo))
            return false

        return this.bundleType == other.bundleType &&
                Arrays.equals(this.services, other.services)
    }

    override fun hashCode(): Int{
        var result = bundleType?.hashCode() ?: 0
        result = 31 * result + Arrays.hashCode(services)
        return result
    }
}