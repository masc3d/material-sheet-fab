package org.deku.leoz.discovery

import org.deku.leoz.bundle.Bundles
import rx.lang.kotlin.PublishSubject
import rx.lang.kotlin.synchronized
import sx.concurrent.Service
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * Discovery service base class
 * Created by masc on 29/08/16.
 * @property port Port to listen on
 * @property bundleType The bundle type this discovery service will expose
 * @property serviceInfos Zeroconf service infos to register
 */
abstract class DiscoveryService(
        executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
        val port: Int,
        val bundleType: Bundles? = null,
        val serviceInfos: List<org.deku.leoz.discovery.ServiceInfo> = arrayListOf())
: Service(
        executorService = executorService) {

    /**
     * Discovery service event type
     */
    enum class EventType {
        Resolved,
        Removed
    }

    /**
     * Discovery service event
     */
    data class Event(val eventType: EventType, val serviceInfo: DiscoveredServiceInfo) {}

    val serviceInfosByHost: MutableMap<String, MutableList<DiscoveredServiceInfo>> = mutableMapOf()

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
}