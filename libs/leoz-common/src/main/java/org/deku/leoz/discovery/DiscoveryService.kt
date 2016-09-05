package org.deku.leoz.discovery

import org.deku.leoz.bundle.BundleType
import rx.lang.kotlin.PublishSubject
import rx.lang.kotlin.synchronized
import sx.Lifecycle
import sx.concurrent.Service
import sx.net.UdpDiscoveryService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * Discovery service base class
 * Created by masc on 29/08/16.
 * @property port Port to listen on
 * @property bundleType The bundle type this discovery service will expose
 * @property serviceInfos Zeroconf service infos to register
 */
class DiscoveryService(
        val port: Int,
        val bundleType: BundleType? = null,
        val serviceInfos: List<org.deku.leoz.discovery.ServiceInfo> = arrayListOf())
: Lifecycle {
    private val _discoveryService: UdpDiscoveryService<ServiceInfo>
    /**
     * Discovery service event type
     */
    enum class EventType {
        Resolved,
        Removed
    }

    init {
        _discoveryService = UdpDiscoveryService(this.port)
    }

    /**
     * Discovery service event
     */
    data class Event(val eventType: EventType, val serviceInfo: ServiceInfo) {}

    val serviceInfosByHost: MutableMap<String, MutableList<ServiceInfo>> = mutableMapOf()

    private val rxOnServiceUpdateSubject = PublishSubject<Event>().synchronized()
    public val rxOnServiceUpdate by lazy { rxOnServiceUpdateSubject.asObservable() }

    /**
     * Derived classes should call this when a service is resolved or removed
     */
    protected fun notifyServiceUpdate(event: Event) {
        synchronized(serviceInfosByHost, {
            var serviceInfos = serviceInfosByHost[event.serviceInfo.host]
            if (serviceInfos == null) {
                serviceInfos = arrayListOf()
                this.serviceInfosByHost[event.serviceInfo.host] = serviceInfos
            }

            val si = serviceInfos.find { it.serviceType == event.serviceInfo.serviceType }
            if (si == null) {
                serviceInfos.add(event.serviceInfo)
                this.rxOnServiceUpdateSubject.onNext(event)
            }
        })
    }

    override fun start() {
        _discoveryService.start()
    }

    override fun stop() {
        _discoveryService.stop()
    }

    override fun restart() {
        _discoveryService.restart()
    }

    override fun isRunning(): Boolean {
        return _discoveryService.isRunning()
    }
}