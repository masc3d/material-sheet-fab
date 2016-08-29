package sx.net

import org.slf4j.LoggerFactory
import sx.Disposable
import sx.LazyInstance
import sx.concurrent.Service
import java.net.*
import java.time.Duration
import java.util.concurrent.Executors

/**
 * Leoz broadcast discovery service
 * Created by masc on 29/08/16.
 */
open class UdpDiscoveryService(val port: Int) :
        Service(
                executorService = Executors.newScheduledThreadPool(2),
                period = Duration.ofSeconds(5)) {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private var running = false

    private val BUFFER_SIZE = 15000
    private val RECEIVE_TIMEOUT = Duration.ofSeconds(2)

    /**
     * Server
     */
    private val server = LazyInstance({ Server() })

    /**
     * Client
     */
    private val client = LazyInstance({ Client() })

    /**
     * Can be override to create a customized request message
     */
    open protected fun createRequest(): ByteArray {
        return "DISCOVERY_REQUEST_MESSAGE".toByteArray()
    }

    /**
     * Can be overridden to create a customized response message
     * @param address Address this response is going to be sent to
     * @param request Request data
     */
    open protected fun createResponse(address: InetAddress, request: ByteArray): ByteArray {
        return "DISCOVERY_RESPONSE_MESSAGE".toByteArray()
    }

    /**
     * Called when a response is received
     * @param address Address of sender
     * @param response Response data
     */
    open protected fun onResponse(address: InetAddress, response: ByteArray) {
        this.log.info("Discovered ${address}")
    }

    /**
     * Discovery server
     */
    private inner class Server : Runnable, Disposable {
        private val log = LoggerFactory.getLogger(this.javaClass)
        private val buffer = ByteArray(BUFFER_SIZE)
        private val socket: DatagramSocket

        init {
            this.socket = DatagramSocket(this@UdpDiscoveryService.port, InetSocketAddress(0).address)
            this.socket.broadcast = true
        }

        override fun run() {
            this.log.trace("Starting discovery host cycle")

            val requestPacket = DatagramPacket(buffer, buffer.size)
            this.socket.receive(requestPacket)

            val response = this@UdpDiscoveryService.createResponse(requestPacket.address, requestPacket.data.copyOf(requestPacket.length))

            val responsePacket = DatagramPacket(response, response.size, requestPacket.address, requestPacket.port)
            this.socket.send(responsePacket)

            log.debug("Answering to [${responsePacket.address.hostAddress}]")
        }

        override fun close() {
            this.socket.close()
        }
    }

    /**
     * Discovery client
     */
    private inner class Client : Runnable, Disposable {
        private val log = LoggerFactory.getLogger(this.javaClass)
        private val buffer = ByteArray(BUFFER_SIZE)
        private val socket = DatagramSocket()

        init {
            this.socket.setBroadcast(true)
            // Set timeout fo receiving response
            this.socket.soTimeout = RECEIVE_TIMEOUT.toMillis().toInt()
        }

        override fun run() {
            this.log.trace("Starting discovery client cycle")
            val sendData = this@UdpDiscoveryService.createRequest()

            // Broadcast the message over all the network interfaces
            NetworkInterface.getNetworkInterfaces().toList()
                    .filter { !it.isLoopback && it.isUp }
                    .flatMap { it.interfaceAddresses }
                    .filter { !it.address.isLoopbackAddress && it.address.isSiteLocalAddress && it.broadcast != null }
                    .forEach {
                        val broadcast = it.broadcast

                        try {
                            val sendPacket = DatagramPacket(sendData, sendData.size, broadcast, this@UdpDiscoveryService.port)
                            this.socket.send(sendPacket)
                            this.log.info("Broadcasting to ${sendPacket.address}")
                        } catch (e: Exception) {
                            this.log.error(e.message, e)
                        }
                    }

            this.log.info("Waiting for replies")

            // Receive packet in a loop until we timeout
            while (true) {
                val receivePacket = DatagramPacket(buffer, buffer.size)

                try {
                    this.socket.receive(receivePacket)
                } catch(e: SocketTimeoutException) {
                    break
                }

                this@UdpDiscoveryService.onResponse(receivePacket.address, receivePacket.data.copyOf(receivePacket.length))
            }
        }

        override fun close() {
            this.socket.close()
        }
    }

    override fun onStart() {
        this.running = true

        this.submitSupplementalTask {
            while (this.running) {
                this.server.get().run()
            }
        }
    }

    override fun run() {
        this.client.get().run()
    }

    override fun onStop(interrupted: Boolean) {
        super.onStop(interrupted)
        this.client.ifSet { it.close() }
        this.client.reset()
        this.server.ifSet { it.close() }
        this.server.reset()
        this.running = false
    }
}