package org.deku.leoz.discovery.impl

import org.deku.leoz.bundle.Bundles
import org.deku.leoz.discovery.DiscoveredServiceInfo
import org.deku.leoz.discovery.DiscoveryService
import org.deku.leoz.discovery.ServiceType
import org.slf4j.LoggerFactory
import sx.LazyInstance
import java.net.InetAddress
import java.net.NetworkInterface
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
 */
class JmDNSDiscoveryService(
        executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
        port: Int,
        bundleType: Bundles? = null,
        serviceInfos: List<org.deku.leoz.discovery.ServiceInfo> = arrayListOf())
: DiscoveryService(
        executorService = executorService,
        port = port,
        bundleType = bundleType,
        serviceInfos = serviceInfos) {

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

    init {
        // The only way to customize jmDNS port for now.
        System.setProperty("net.mdns.port", port.toString())
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
            val local: Boolean = this@JmDNSDiscoveryService.jmmdns.dns.map { it.name }.contains(event.info.server)
            log.debug("serviceResolved local=${local} ${event.info.server} ${event.info.hostAddresses.joinToString(",")}")

            // Parse service type
            val serviceType: ServiceType?
            try {
                serviceType = ServiceType.values().find { it.value == event.info.application }
            } catch (e: IllegalArgumentException) {
                serviceType = null
            }
            if (serviceType == null) {
                log.warn("Unknown service type [${event.type}]")
            }

            // Parse subtypes/bundle type
            var bundleType: Bundles? = null
            val subtypes = event.info.subtype.split(',').forEach { subtype ->
                try {
                    bundleType = Bundles.values().find { it.value == subtype }
                } catch(e: IllegalArgumentException) {
                }
            }
            if (bundleType == null) {
                log.warn("Bundle type could not be determined from subtypes [${event.info.subtype}]")
            }

            if (serviceType != null && bundleType != null) {
                this@JmDNSDiscoveryService.notifyServiceUpdate(Event(EventType.Resolved, DiscoveredServiceInfo(
                        serviceType = serviceType,
                        port = event.info.port,
                        host = (event.info.inet4Addresses.firstOrNull() ?: event.info.inet6Addresses.firstOrNull())!!.hostName,
                        bundleType = Bundles.LEOZ_NODE
                )))
            }
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
            this@JmDNSDiscoveryService.jmmdns.addServiceListener(si.type, serviceListener)

            // Register and force persistent updates
            // TODO: figure out why jmDNS is r{eally slow on those operations (even much worse on Windows)
            this@JmDNSDiscoveryService.jmmdns.registerService(si)
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