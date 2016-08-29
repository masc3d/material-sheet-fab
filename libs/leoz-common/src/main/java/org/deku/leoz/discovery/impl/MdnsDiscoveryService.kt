package org.deku.leoz.discovery.impl

import org.deku.leoz.bundle.BundleType
import org.deku.leoz.discovery.DiscoveryService
import org.deku.leoz.discovery.ServiceInfo
import org.deku.leoz.discovery.ServiceType
import org.slf4j.LoggerFactory
import org.xbill.DNS.*
import org.xbill.mDNS.Lookup
import org.xbill.mDNS.MulticastDNSService
import org.xbill.mDNS.ServiceInstance
import org.xbill.mDNS.ServiceName
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * Created by masc on 29/08/16.
 */
class MdnsDiscoveryService(executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
                           port: Int,
                           bundleType: BundleType?,
                           serviceInfos: List<ServiceInfo> = arrayListOf()) :
        DiscoveryService(
                executorService = executorService,
                port = port,
                bundleType = bundleType,
                serviceInfos = serviceInfos) {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private var mDnsService: MulticastDNSService? = null
    private var lookup: Lookup? = null
    private val registeredServices = mutableListOf<ServiceInstance>()

    private fun ServiceInfo.toMdnsType(): String {
        return "${this.serviceType.name}._${this.serviceType.value}._tcp.local."
    }

    private fun registerServices() {
        val addresses = NetworkInterface.getNetworkInterfaces().toList().flatMap {
            it.interfaceAddresses.map { it.address }.filter { !it.isLoopbackAddress && it.isSiteLocalAddress }
        }

        this.serviceInfos.forEach {
            log.info("Registering [${it.serviceType}]")
            val service = ServiceInstance(
                    ServiceName(it.toMdnsType()),
                    0,
                    0,
                    it.port,
                    Name.fromString(InetAddress.getLocalHost().hostName + "."),
                    addresses.toTypedArray(),
                    arrayListOf<String>() as Collection<*>)

            mDnsService!!.register(service)
        }
    }

    override fun onStart() {
        this.mDnsService = MulticastDNSService()
        this.registerServices()

        log.info("Creating lookup")
        val lookup = Lookup(this.serviceInfos.map { it.toMdnsType() }.toTypedArray(), Type.ANY, DClass.IN)
        lookup.lookupRecordsAsync(object : Lookup.RecordListener {
            override fun receiveRecord(id: Any, record: Record) {
                log.info("Record Found: ${record}")
            }

            override fun handleException(id: Any, e: Exception) {
                log.error(e.message, e)
            }
        })
        this.lookup = lookup
    }

    override fun onStop(interrupted: Boolean) {
        this.lookup?.close()
        this.mDnsService?.close()
        this.mDnsService = null
        this.lookup = null
    }
}