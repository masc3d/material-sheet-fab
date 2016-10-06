package org.deku.leoz.discovery

import org.deku.leoz.Identity
import org.deku.leoz.bundle.BundleType
import rx.lang.kotlin.PublishSubject
import rx.lang.kotlin.synchronized
import sx.Lifecycle
import sx.concurrent.Service
import sx.net.UdpDiscoveryService
import java.util.*
import java.util.concurrent.Executors
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
        val bundleType: BundleType)
: UdpDiscoveryService<DiscoveryInfo>(
        executorService = executorService,
        nodeUid = uid,
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