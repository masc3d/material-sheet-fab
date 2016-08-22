package org.deku.leoz.discovery

import org.deku.leoz.bundle.Bundles
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.Lifecycle
import sx.logging.slf4j.info
import java.net.InetAddress
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener
import javax.jmdns.ServiceTypeListener

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

    private val serviceInfos: Array<out ServiceInfo>

    init {
        System.setProperty("net.mdns.port", port.toString())
        this.serviceInfos = serviceInfos
    }

    /** JmDns instance.
     * The instance state of also indicates if this service is started/running or not
     **/
    private val lazyJmdns = LazyInstance<JmDNS>({ JmDNS.create(InetAddress.getLocalHost()) })
    private val jmdns: JmDNS get() = lazyJmdns.get()

    /**
     * JmDns/zeroconf service type listener
     */
    private val serviceTypeListener = object : ServiceTypeListener {
        override fun subTypeForServiceTypeAdded(event: ServiceEvent?) {
            log.trace("subTypeForServiceTypeAdded ${event}")
        }

        override fun serviceTypeAdded(event: ServiceEvent?) {
            log.trace("serviceTypeAdded ${event}")
            jmdns.addServiceListener(event!!.type, serviceListener)
        }
    }

    private val serviceListener = object : ServiceListener {
        override fun serviceResolved(event: ServiceEvent?) {
            log.trace("serviceResolved ${event}")
        }

        override fun serviceRemoved(event: ServiceEvent?) {
            log.trace("serviceRemoved ${event}")
        }

        override fun serviceAdded(event: ServiceEvent?) {
            log.trace("serviceAdded ${event}")
        }

    }

    override fun start() {
        if (lazyJmdns.isSet) {
            // Already running
            return
        }

        log.trace("Starting discovery service")

        try {
            // Add service type listener
            this.jmdns.addServiceTypeListener(this.serviceTypeListener)

            // Map to JmDns service types and register
            this.serviceInfos.map {
                val type = "_${it.serviceType.value}._tcp."
                val name = it.serviceType.value
                val subtype = bundleType?.value ?: ""
                val text = ""
                javax.jmdns.ServiceInfo.create(type, name, subtype, port, text)
            }.forEach {
                log.trace("Registering ${it}")
                this.jmdns.registerService(it)
                log.trace("Registered ${it}")
            }
        } catch(e: Throwable) {
            this.lazyJmdns.reset()
            throw e
        }
    }

    override fun stop() {
        this.lazyJmdns.ifSet {
            log.info("Stopping discovery service")
            it.close()
        }
        this.lazyJmdns.reset()
    }

    override fun restart() {
        this.stop()
        this.start()
    }

    override fun isRunning(): Boolean {
        return this.lazyJmdns.isSet
    }
}