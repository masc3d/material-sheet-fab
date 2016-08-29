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

    inner class Server : Runnable {
        private val log = LoggerFactory.getLogger(this.javaClass)

        override fun run() {
            try {
                val serverSocket = this@BroadcastDiscoveryService.serverSocket!!
                while (this@BroadcastDiscoveryService.running) {
                    this.log.trace("Starting discovery host cycle")

                    val recvBuf = ByteArray(15000)
                    val packet = DatagramPacket(recvBuf, recvBuf.size)
                    serverSocket.receive(packet)

                    val message = String(packet.data, 0, packet.length, Charset.defaultCharset())
                    this.log.debug("Discovery request received from [${packet.address.hostAddress}] data [${message}]")

                    if (message == REQUEST) {
                        val sendData = RESPONSE.toByteArray()

                        val sendPacket = DatagramPacket(sendData, sendData.size, packet.getAddress(), packet.getPort())
                        serverSocket.send(sendPacket)

                        log.debug("Sent reply to [${sendPacket.address.hostAddress}]")
                    }
                }
            } catch (e: Exception) {
                this.log.error(e.message, e)
            }
        }
    }

    override fun onStart() {
        this.running = true

        val serverSocket = DatagramSocket(this.port, InetSocketAddress(0).address)
        serverSocket.broadcast = true
        this.serverSocket = serverSocket

        val server = Server()
        this.submitSupplementalTask {
            server.run()
        }

        this.submitSupplementalTask {
            val clientSocket = DatagramSocket()
            clientSocket.setBroadcast(true)
            clientSocket.soTimeout = 2000
            val recvBuf = ByteArray(15000)

            try {
                while (this.running) {
                    log.trace("Starting discovery client cycle")
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

                    try {
                        while(true) {
                            val receivePacket = DatagramPacket(recvBuf, recvBuf.size)

                            try {
                                clientSocket.receive(receivePacket)
                            } catch(e: SocketTimeoutException) {
                                break
                            }

                            log.trace("Received response from [${receivePacket.address.hostAddress}]")

                            val message = String(receivePacket.data, 0, receivePacket.length, Charset.defaultCharset())
                            if (message == RESPONSE) {
                                log.info("Resolved ${receivePacket.address}")
                            }
                        }
                    } catch(e: Exception) {
                        log.error(e.message, e)
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