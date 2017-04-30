package org.deku.leoz.service.internal

import org.deku.leoz.bundle.BundleType
import org.deku.leoz.service.entity.internal.discovery.DiscoveryInfo
import sx.net.UdpDiscoveryService
import java.util.*
import java.util.concurrent.ScheduledExecutorService

/**
 * Discovery service base class
 * Created by masc on 29/08/16.
 * @property port Port to listen on
 * @property bundleType The bundle type this discovery service will expose
 */
class DiscoveryService(
        executorService: ScheduledExecutorService,
        port: Int = DiscoveryService.DEFAULT_PORT,
        uid: String = UUID.randomUUID().toString(),
        val bundleType: BundleType,
        passive: Boolean = false)
: UdpDiscoveryService<DiscoveryInfo>(
        executorService = executorService,
        uid = uid,
        passive = passive,
        infoClass = DiscoveryInfo::class.java,
        port = port) {

    companion object {
        const val DEFAULT_PORT = 13004
    }

    init {
        this.nodeInfo = DiscoveryInfo(bundleType = bundleType)
    }

    /**
     * Adds services
     */
    fun addServices(vararg services: DiscoveryInfo.Service) {
        val finalServices = info?.services?.toMutableList() ?: mutableListOf()

        finalServices.addAll(services)

        this.nodeInfo = DiscoveryInfo(
                this.bundleType,
                finalServices.toTypedArray())
    }
}