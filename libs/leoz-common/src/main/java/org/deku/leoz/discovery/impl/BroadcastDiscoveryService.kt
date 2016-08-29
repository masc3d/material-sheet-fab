package org.deku.leoz.discovery.impl

import org.deku.leoz.bundle.BundleType
import org.deku.leoz.discovery.DiscoveryService
import org.deku.leoz.discovery.ServiceInfo
import org.slf4j.LoggerFactory
import java.net.*
import java.nio.charset.Charset
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * Leoz broadcast discovery service
 * Created by masc on 29/08/16.
 */
class BroadcastDiscoveryService(port: Int,
                                bundleType: BundleType?,
                                serviceInfos: List<ServiceInfo> = arrayListOf()) :
        DiscoveryService(
                executorService = Executors.newScheduledThreadPool(2),
                port = port,
                bundleType = bundleType,
                serviceInfos = serviceInfos) {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private var running = false
    private var serverSocket: DatagramSocket? = null

    private val REQUEST = "DISCO_REQUEST";
    private val RESPONSE = "DISCO_RESPONSE"

    override fun onStart() {
        this.running = true

        val serverSocket = DatagramSocket(this.port, InetSocketAddress(0).address)
        serverSocket.broadcast = true
        this.serverSocket = serverSocket

        this.submitSupplementalTask {
            try {
                while (this.running) {
                    log.info("Starting discovery host cycle")

                    val recvBuf = ByteArray(15000)
                    val packet = DatagramPacket(recvBuf, recvBuf.size)
                    serverSocket.receive(packet)

                    val message = String(packet.data, 0, packet.length, Charset.defaultCharset())
                    log.info("Discovery request packet received from [${packet.address.hostAddress}] data [${message}]")

                    if (message == REQUEST) {
                        val sendData = RESPONSE.toByteArray()

                        val sendPacket = DatagramPacket(sendData, sendData.size, packet.getAddress(), packet.getPort())
                        serverSocket.send(sendPacket)

                        log.info("Sent reply to [${sendPacket.address.hostAddress}]")
                    }
                }
            } catch (e: Exception) {
                this.log.error(e.message, e)
            }
        }

        this.submitSupplementalTask {
            val clientSocket = DatagramSocket()
            clientSocket.setBroadcast(true)

            try {
                while (this.running) {
                    log.info("Starting discovery client cycle")
                    val sendData = REQUEST.toByteArray()

                    // Broadcast the message over all the network interfaces
                    NetworkInterface.getNetworkInterfaces().toList()
                            .filter { !it.isLoopback && it.isUp }
                            .flatMap { it.interfaceAddresses }
                            .filter { !it.address.isLoopbackAddress && it.address.isSiteLocalAddress && it.broadcast != null }
                            .forEach {
                                val broadcast = it.broadcast

                                try {
                                    val sendPacket = DatagramPacket(sendData, sendData.size, broadcast, this.port)
                                    clientSocket.send(sendPacket)
                                    log.info("Discovery request sent to ${sendPacket.address}")
                                } catch (e: Exception) {
                                    log.error(e.message, e)
                                }
                    }

                    log.info("Waiting for host reply")

                    val recvBuf = ByteArray(15000)
                    val receivePacket = DatagramPacket(recvBuf, recvBuf.size)
                    clientSocket.receive(receivePacket)

                    log.info("Received response from [${receivePacket.address.hostAddress}]")

                    val message = String(receivePacket.data, 0, receivePacket.length, Charset.defaultCharset())
                    if (message == RESPONSE) {
                        log.info("Resolved ${receivePacket.address}")
                    }

                    Thread.sleep(2000)
                }
            } catch (e: Exception) {
                log.error(e.message, e)
            } finally {
                clientSocket.close()
            }
        }
    }

    override fun onStop(interrupted: Boolean) {
        super.onStop(interrupted)
        this.serverSocket?.close()
        this.running = false
    }
}