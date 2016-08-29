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

        // Keep a socket open to listen to all the UDP trafic that is destined for this port
        val serverSocket = DatagramSocket(this.port, InetSocketAddress(0).address)
        serverSocket.broadcast = true
        this.serverSocket = serverSocket

        this.submitSupplementalTask {
            try {
                while (this.running) {
                    log.info("Starting discovery host cycle")

                    //Receive a packet
                    val recvBuf = ByteArray(15000)
                    val packet = DatagramPacket(recvBuf, recvBuf.size)
                    serverSocket.receive(packet)

                    val packetText = String(packet.data, 0, packet.length, Charset.defaultCharset())
                    //Packet received
                    log.info("Discovery request packet received from [${packet.address.hostAddress}] data [${packetText}]")

                    //See if the packet holds the right command (message)
                    val message = String(packet.getData()).trim({ it <= ' ' })
                    if (message == REQUEST) {
                        val sendData = RESPONSE.toByteArray()

                        //Send a response
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
            // Find the server using UDP broadcast
            //Open a random port to send the package
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

                                // Send the broadcast package!
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

                    //Check if the message is correct
                    val message = String(receivePacket.data).trim { it <= ' ' }
                    if (message == RESPONSE) {
                        //DO SOMETHING WITH THE SERVER'S IP (for example, store it in your controller)
                        log.info("Resolved ${receivePacket.address}")
                    }

                    Thread.sleep(2000)
                }

                //Close the port!
                clientSocket.close()
            } catch (e: Exception) {
                log.error(e.message, e)
            }

        }
    }

    override fun onStop(interrupted: Boolean) {
        super.onStop(interrupted)
        this.serverSocket?.close()
        this.running = false
    }
}