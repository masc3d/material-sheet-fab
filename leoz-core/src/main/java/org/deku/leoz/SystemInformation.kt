package org.deku.leoz

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.google.common.base.CharMatcher
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.net.*
import java.util.*

/**
 * Generic system information
 * Created by masc on 01.07.15.
 */
@JsonPropertyOrder("hardwareAddress", "hostname", "networkAddresses")
class SystemInformation : Serializable {

    var hostname: String = ""
        private set
    var hardwareAddress: String = ""
        private set
    var networkAddresses: List<String> = ArrayList()
        private set

    companion object {
        private const val serialVersionUID = 1558995436722991648L

        /** Logger */
        private val log = LoggerFactory.getLogger(SystemInformation::class.java)

        /**
         * Wrapper for releveant network interface/address info
         */
        private data class InterfaceAddressInfo(
                val networkInterface: NetworkInterface,
                val interfaceAddress: InterfaceAddress) {
        }

        /**
         * Network interfaces
         */
        private val interfaces by lazy {
            NetworkInterface.getNetworkInterfaces().toList()
                    // Filter interfaces
                    .filter { !it.isLoopback && it.isUp && !it.isVirtual && !it.isPointToPoint }
                    // Map relevant information to InterfaceAddressInfo
                    .flatMap { it.interfaceAddresses.map { ia -> InterfaceAddressInfo(it, ia) } }
                    // Filter by address criteria
                    .filter {
                        !it.interfaceAddress.address.isLoopbackAddress &&
                                it.interfaceAddress.address.isSiteLocalAddress &&
                                it.interfaceAddress.broadcast != null
                    }
                    .sortedBy { it.networkInterface.index }
                    .toTypedArray()
        }

        /**
         * Find appropriate ipv4 address
         * @param networkInterface
         * *
         * @return
         */
        private fun findIpv4Address(networkInterface: NetworkInterface): Inet4Address? {
            return networkInterface.inetAddresses.toList().filterIsInstance<Inet4Address>().firstOrNull()
        }

        /**
         * Find apporopriate ipv6 address
         * @param networkInterface
         * *
         * @return
         */
        private fun findIpv6Address(networkInterface: NetworkInterface): Inet6Address? {
            return networkInterface.inetAddresses.toList().filterIsInstance<Inet6Address>().firstOrNull { li -> !li.isLinkLocalAddress }
        }

        @JvmStatic fun create(): SystemInformation {
            var hostname: String = ""
            var hardwareAddress: String = ""

            val addresses = ArrayList<InetAddress>()

            var networkInterface: NetworkInterface? = null
            var host: InetAddress? = null

            // Fast interface lookup using .getLocalHost(). Works pretty well on OSX/Windows
            try {
                val localhost = InetAddress.getLocalHost()
                if (!localhost.isLoopbackAddress) {
                    host = localhost
                    networkInterface = NetworkInterface.getByInetAddress(host)
                }
            } catch (e: Exception) {
                log.warn(e.message)
            }

            // Some (eg. linux) systems require a more refined lookup
            if (networkInterface == null) {
                try {
                    for (nii in Collections.list(NetworkInterface.getNetworkInterfaces())) {
                        if (nii.isUp && !nii.isLoopback && !nii.isPointToPoint) {
                            networkInterface = nii
                            host = findIpv4Address(nii)
                            break
                        }
                    }
                } catch (e: Exception) {
                    log.warn(e.message)
                }
            }

            if (networkInterface != null) {
                log.info("Network interface [${networkInterface}] [${host}]")

                try {
                    val ipv4: Inet4Address?
                    val ipv6: Inet6Address?

                    // Hardware address
                    // Format hardware address
                    hardwareAddress = networkInterface.hardwareAddress.map { c -> java.lang.String.format("%02x", c) }.joinToString(":")

                    // Hostname. getHostName()/getCanonicalHostName() as it does connection check/DNS resolve
                    // and may be slow in some scenarios (eg. windows). toString() is good enough and
                    // returns hostname without connection check/lookup
                    hostname = host!!.hostName

                    // Find network interface addresses
                    ipv4 = findIpv4Address(networkInterface)
                    if (ipv4 != null)
                        addresses.add(ipv4)

                    ipv6 = findIpv6Address(networkInterface)
                    if (ipv6 != null)
                        addresses.add(ipv6)
                } catch (e: Exception) {
                    log.warn(e.message)
                }
            }

            val si = SystemInformation()
            si.hardwareAddress = hardwareAddress
            si.hostname = hostname
            si.networkAddresses = addresses.map({ a -> CharMatcher.`is`('/').trimLeadingFrom(a.toString()) })

            return si
        }
    }

    override fun toString(): String {
        return "System information: hardware address [${hardwareAddress}] hostname [${hostname}] network addresses [${networkAddresses.joinToString(", ")}]"
    }
}
