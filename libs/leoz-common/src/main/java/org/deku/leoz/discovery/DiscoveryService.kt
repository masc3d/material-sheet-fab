package org.deku.leoz.discovery

import org.deku.leoz.bundle.Bundles
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.Lifecycle
import java.net.InetAddress
import java.net.NetworkInterface
import javax.jmdns.JmmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener
import javax.jmdns.ServiceTypeListener
import javax.jmdns.impl.NetworkTopologyDiscoveryImpl

/**
 * Leoz discovery service
 * Created by masc on 22/08/16.
 * @property port Port to listen on
 * @property bundleType The bundle type this discovery service will (optionally) expose
 * @property serviceInfos Zeroconf service infos to register
 */
class DiscoveryService(
        val port: Int,
        val bundleType: Bundles? = null,
        vararg serviceInfos: ServiceInfo) : Lifecycle {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /** Discoverable service infos */
    private val serviceInfos: Array<out ServiceInfo>

    init {
        // The only way to customize jmDNS port for now.
        System.setProperty("net.mdns.port", port.toString())
        this.serviceInfos = serviceInfos
    }

    /**
     * jmmDNS multihomed instance
     * The instance state of also indicates if this service is started/running or not
     */
    private val lazyJmmdns = LazyInstance<JmmDNS>({ JmmDNS.Factory.getInstance() })
    private val jmmdns: JmmDNS get() = lazyJmmdns.get()

    /**
     * jmDNS service type listener
     */
    private val serviceTypeListener = object : ServiceTypeListener {
        override fun subTypeForServiceTypeAdded(event: ServiceEvent) {
            log.debug("subTypeForServiceTypeAdded ${event.type}")
        }

        override fun serviceTypeAdded(event: ServiceEvent) {
            log.debug("serviceTypeAdded ${event.type}")
            this@DiscoveryService.jmmdns.addServiceListener(event.type, serviceListener)
        }
    }

    /**
     * jmDNS service listener
     */
    private val serviceListener = object : ServiceListener {
        override fun serviceResolved(event: ServiceEvent) {
            log.debug("serviceResolved ${event.info.hostAddresses.joinToString(",")}")
        }

        override fun serviceRemoved(event: ServiceEvent) {
            log.debug("serviceRemoved ${event.info.hostAddresses.joinToString(",")}")
        }

        override fun serviceAdded(event: ServiceEvent) {
            log.debug("serviceAdded ${event.dns.hostName} ${event.type}")
        }
    }

    /**
     * Applies service configuration ot jmdns.
     * This method may be called repeatedly, eg., when network topology changes.
     */
    private fun configureServices() {
        // REMARK: it's safe to call .addXXX methods of jmDNS multiple times as they check for duplicate listeners/services

        // Add service type listener
        this.jmmdns.addServiceTypeListener(this.serviceTypeListener)

        this.serviceInfos.map {
            // Translate generic service info to jmDNS service info
            val type = "_${it.serviceType.value}._tcp."
            val name = it.serviceType.value
            val subtype = bundleType?.value ?: ""
            val text = ""
            javax.jmdns.ServiceInfo.create(type, name, subtype, port, text)
        }.forEach {
            // Register and force persistent updates
            // TODO: figure out why jmDNS is r{eally slow on those operations (even much worse on Windows)
            this.jmmdns.registerService(it)
            this.jmmdns.requestServiceInfo(it.type, "", true)
        }
    }

    /**
     * Network topology discovery, customizing network interface/address filter
     */
    class NetworkTopologyDiscovery : NetworkTopologyDiscoveryImpl() {
        private val log = LoggerFactory.getLogger(this.javaClass)

        override fun useInetAddress(networkInterface: NetworkInterface?, interfaceAddress: InetAddress?): Boolean {
            if (interfaceAddress == null)
                return false

            val use = super.useInetAddress(networkInterface, interfaceAddress)
            log.trace("use=${use} ${networkInterface} ${interfaceAddress} ${interfaceAddress.isLinkLocalAddress} ${interfaceAddress.isSiteLocalAddress}")

            // Only listen on private/site local addresses
            if (!interfaceAddress.isSiteLocalAddress)
                return false

            return use
        }
    }

    /**
     * Start discovery service
     */
    override fun start() {
        if (lazyJmmdns.isSet) {
            // Already running
            return
        }

        log.info("Starting discovery service")

        javax.jmdns.NetworkTopologyDiscovery.Factory.setClassDelegate {
            NetworkTopologyDiscovery()
        }

        try {
            this.configureServices()
        } catch(e: Throwable) {
            this.lazyJmmdns.reset()
            throw e
        }

        log.info("Started discovery service")
    }

    /**
     * Stop discovery service
     */
    override fun stop() {
        this.lazyJmmdns.ifSet {
            log.info("Stopping discovery service")
            it.close()
            log.info("Stopped discovery service")
        }
        this.lazyJmmdns.reset()
    }

    /**
     * Restart discovery service
     */
    override fun restart() {
        this.stop()
        this.start()
    }

    /**
     * Indicates if discovery service is running
     */
    override fun isRunning(): Boolean {
        return this.lazyJmmdns.isSet
    }
}