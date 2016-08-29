package org.deku.leoz.discovery.impl

import org.deku.leoz.bundle.BundleType
import org.deku.leoz.discovery.DiscoveryService
import org.deku.leoz.discovery.ServiceInfo
import org.slf4j.LoggerFactory
import sx.net.UdpDiscoveryService
import java.net.*
import java.nio.charset.Charset
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * Leoz broadcast discovery service
 * Created by masc on 29/08/16.
 */
class BroadcastDiscoveryService(port: Int,
                                val bundleType: BundleType?,
                                val serviceInfos: List<ServiceInfo> = arrayListOf()) :
        UdpDiscoveryService(port)
{
    private val log = LoggerFactory.getLogger(this.javaClass)
}