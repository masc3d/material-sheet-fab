package org.deku.leoz.node

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.google.common.base.CharMatcher
import com.google.common.collect.Lists
import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import java.io.Serializable
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.ArrayList
import java.util.Collections
import java.util.stream.Collectors

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
        @JvmStatic private const val serialVersionUID = 1558995436722991648L

        /** Logger */
        private val log = LogFactory.getLog(SystemInformation::class.java)

        /**
         * Find appropriate ipv4 address
         * @param networkInterface
         * *
         * @return
         */
        private fun findIpv4Address(networkInterface: NetworkInterface): Inet4Address? {
            return networkInterface.inetAddresses.toList().firstOrNull { li -> li is Inet4Address } as Inet4Address?
        }

        /**
         * Find apporopriate ipv6 address
         * @param networkInterface
         * *
         * @return
         */
        private fun findIpv6Address(networkInterface: NetworkInterface): Inet6Address? {
            return networkInterface.inetAddresses.toList().firstOrNull { li -> li is Inet6Address && !li.isLinkLocalAddress } as Inet6Address?
        }

        @JvmStatic fun create(): SystemInformation {
            var hostname: String = ""
            var hardwareAddress: String = ""
            var ipv4: Inet4Address?
            var ipv6: Inet6Address?

            val addresses = ArrayList<InetAddress>()

            var networkInterface: NetworkInterface? = null
            var localhost: InetAddress? = null
            try {
                localhost = InetAddress.getLocalHost()
                networkInterface = NetworkInterface.getByInetAddress(localhost)
            } catch (e: Exception) {
                log.warn(e.getMessage(), e)
            }

            if (networkInterface == null) {
                log.warn("No network interface referring to host name")

                try {
                    for (nii in Collections.list(NetworkInterface.getNetworkInterfaces())) {
                        if (nii.isUp && !nii.isLoopback) {
                            networkInterface = nii
                            localhost = findIpv4Address(nii)
                            break
                        }
                    }
                } catch (e: Exception) {
                    log.warn(e.getMessage(), e)
                }

            }

            if (networkInterface != null) {
                try {
                    // Hardware address
                    val hwAddressParts = Lists.newArrayList(*ArrayUtils.toObject(
                            networkInterface.hardwareAddress))

                    // Format hardware address
                    hardwareAddress = hwAddressParts.map { c -> java.lang.String.format("%02x", c) }.joinToString(":")

                    // Hostname. getHostName()/getCanonicalHostName() as it does connection check/DNS resolve
                    // and may be slow in some scenarios (eg. windows). toString() is good enough and
                    // returns hostname without connection check/lookup
                    hostname = localhost!!.hostName

                    // Find network interface addresses
                    ipv4 = findIpv4Address(networkInterface)
                    if (ipv4 != null)
                        addresses.add(ipv4)

                    ipv6 = findIpv6Address(networkInterface)
                    if (ipv6 != null)
                        addresses.add(ipv6)
                } catch (e: Exception) {
                    log.warn(e.getMessage(), e)
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
