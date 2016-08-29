package org.deku.leoz.discovery.impl

import org.deku.leoz.bundle.BundleType
import org.deku.leoz.discovery.DiscoveryService
import org.deku.leoz.discovery.ServiceInfo
import org.slf4j.LoggerFactory
import org.xbill.DNS.Name
import org.xbill.DNS.TXTRecord
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
    private val registeredServices = mutableListOf<ServiceInstance>()

    private fun registerServices() {
        log.info("Registering services")
        val addresses = NetworkInterface.getNetworkInterfaces().toList().flatMap {
            it.interfaceAddresses.map { it.address }.filter { !it.isLoopbackAddress && it.isSiteLocalAddress }
        }.subList(0, 1)

        this.serviceInfos.subList(0,1).forEach {
            log.info("FU")
            val service = ServiceInstance(
                    ServiceName("${it.serviceType.name}._${it.serviceType.value}._tcp.local."),
                    0,
                    0,
                    it.port,
                    Name.fromString(InetAddress.getLocalHost().hostName + "."),
                    addresses.toTypedArray(),
                    arrayListOf<String>() as Collection<*>)

            this.registeredServices.add(mDnsService!!.register(service))
        }
        log.info("Registered services")
    }

    override fun onStart() {
        this.mDnsService = MulticastDNSService()
        this.registerServices()
    }

    override fun onStop(interrupted: Boolean) {
        mDnsService?.close()
        mDnsService = null
    }
}