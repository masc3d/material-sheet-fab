package org.deku.leoz.discovery

import org.deku.leoz.bundle.Bundles
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.concurrent.Service
import java.net.InetAddress
import java.net.NetworkInterface
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
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
        executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
        val port: Int,
        val bundleType: Bundles? = null,
        vararg serviceInfos: ServiceInfo)
: Service(
        executorService = executorService) {

    /** Logger */
    private val log = LoggerFactory.getLogger(this.javaClass)

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
        }
    }

    /**
     * jmDNS service listener
     */
    private val serviceListener = object : ServiceListener {
        override fun serviceResolved(event: ServiceEvent) {
            val local: Boolean = this@DiscoveryService.jmmdns.dns.map { it.name }.contains(event.info.server)
            log.debug("serviceResolved local=${local} ${event.info.server} ${event.info.hostAddresses.joinToString(",")}")
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

        val jmsis = this.serviceInfos.map {
            // Translate generic service info to jmDNS service info
            val type = "_${it.serviceType.value}._tcp."
            val name = it.serviceType.value
            val subtype = bundleType?.value ?: ""
            val text = ""
            javax.jmdns.ServiceInfo.create(type, name, subtype, port, text)
        }

        jmsis.forEach { si ->
            this@DiscoveryService.jmmdns.addServiceListener(si.type, serviceListener)

            // Register and force persistent updates
            // TODO: figure out why jmDNS is r{eally slow on those operations (even much worse on Windows)
            this@DiscoveryService.jmmdns.registerService(si)
        }

        jmsis.forEach {
            var i = 1
            this@DiscoveryService.jmmdns.list(it.type, 2000).forEach { si ->
                val local: Boolean = this@DiscoveryService.jmmdns.dns.map { it.name }.contains(si.server)
                log.debug("list ${i++} local=${local} ${si.hostAddresses.joinToString(",")}")
            }
        }
    }

    override fun run() {
    }

    override fun onStart() {
        this.submitSupplementalTask {
            log.info("Configuring mDNS services")
            javax.jmdns.NetworkTopologyDiscovery.Factory.setClassDelegate {
                NetworkTopologyDiscovery()
            }

            this.configureServices()
            log.info("Configured mDNS services")
        }
    }

    override fun onStop(interrupted: Boolean) {
        this.lazyJmmdns.ifSet {
            it.close()
        }
        this.lazyJmmdns.reset()
    }
}