package sx.net

import org.slf4j.LoggerFactory
import sx.Disposable
import sx.LazyInstance
import sx.concurrent.Service
import sx.io.serialization.*
import java.io.Serializable
import java.net.*
import java.time.Duration
import java.util.*
import java.util.concurrent.Executors

/**
 * Lightweight universal udp discovery service
 * @param TInfo Info block type. The class must be serializable and should implement `.equals` for change notifications
 * @property port Port to listen on
 * @param serializer (Optional) override serializer
 * Created by masc on 29/08/16.
 */
open class UdpDiscoveryService<TInfo>(
        val port: Int,
        private val serializer: Serializer = KryoSerializer())
:
        Service(
                executorService = Executors.newScheduledThreadPool(2),
                period = Duration.ofSeconds(5)) where TInfo : Serializable {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private var running = false

    private val BUFFER_SIZE = 16 * 1024
    private val RECEIVE_TIMEOUT = Duration.ofSeconds(2)

    /**
     * Host discovery info
     * @param addressData
     */
    class Host<TInfo>(
            address: InetAddress? = null,
            val info: TInfo? = null) : Serializable {

        private val addressData: ByteArray

        init {
            this.addressData = address?.address ?: ByteArray(0)
        }

        @delegate:Transient
        private val address by lazy { InetAddress.getByAddress(this.addressData) }

        override fun toString(): String{
            return "Host(address=${this.address}, info=$info)"
        }

        override fun equals(other: Any?): Boolean {
            if (other == null || !(other is Host<*>))
                return false

            if (!this.address.equals(other.address))
                return false

            if (this.info == null && other.info == null)
                return true

            return (this.info != null && other.info != null && this.info.equals(other.info))
        }

        override fun hashCode(): Int{
            var result = info?.hashCode() ?: 0
            result = 31 * result + Arrays.hashCode(addressData)
            return result
        }
    }

    /**
     * Server
     */
    private val server = LazyInstance({ Server() })

    /**
     * Client
     */
    private val client = LazyInstance({ Client() })

    /**
     * Can be overridden to create a customized response message
     * @param address Address this response is going to be sent to
     * @param request Request data
     */
    open protected fun createPacket(address: InetAddress): ByteArray {
        val host = Host<TInfo>(address, this.createInfo(address))
        return this.serializer.serializeToByteArray(host)
    }

    /**
     * Can be overridden to create a customized discovery info block
     */
    open protected fun createInfo(address: InetAddress): TInfo? {
        return null
    }

    /**
     * Called when a response is received
     * @param address Address of sender
     * @param response Response data
     */
    @Suppress("UNCHECKED_CAST")
    open protected fun onResponse(address: InetAddress, response: ByteArray) {
        val host = this.serializer.deserializeFrom(response)
        this.onInfo(host as Host<TInfo>)
    }

    open protected fun onInfo(host: Host<TInfo>) {
        this.log.info("Discovered ${host}")
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

            val response = this@UdpDiscoveryService.createPacket(requestPacket.address)

            log.debug("Answering to ${requestPacket.address} size [${response.size}]")

            val responsePacket = DatagramPacket(response, response.size, requestPacket.address, requestPacket.port)
            this.socket.send(responsePacket)
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

            // Broadcast the message over all the network interfaces
            NetworkInterface.getNetworkInterfaces().toList()
                    .filter { !it.isLoopback && it.isUp }
                    .flatMap { it.interfaceAddresses }
                    .filter { !it.address.isLoopbackAddress && it.address.isSiteLocalAddress && it.broadcast != null }
                    .forEach {
                        val broadcast = it.broadcast

                        try {
                            val sendData = this@UdpDiscoveryService.createPacket(it.address)
                            val sendPacket = DatagramPacket(sendData, sendData.size, broadcast, this@UdpDiscoveryService.port)
                            this.socket.send(sendPacket)
                            this.log.info("Broadcasting to ${sendPacket.address}")
                        } catch (e: Exception) {
                            this.log.error(e.message, e)
                        }
                    }

            this.log.info("Waiting for replies")

            // Receive packet in a loop until we timeout

            val repliesByAddress = mutableMapOf<InetAddress, ByteArray>()
            while (true) {
                val receivePacket = DatagramPacket(buffer, buffer.size)

                try {
                    this.socket.receive(receivePacket)
                    if (repliesByAddress.contains(receivePacket.address)) {
                        this.log.info("Discarding previous duplicate reply from [${receivePacket.address}]")
                    }
                    repliesByAddress[receivePacket.address] = receivePacket.data.copyOf(receivePacket.length)
                } catch(e: SocketTimeoutException) {
                    break
                }

                repliesByAddress.forEach {
                    this@UdpDiscoveryService.onResponse(it.key, it.value)
                }
            }
        }

        override fun close() {
            this.socket.close()
        }
    }

    /**
     * On service starr
     */
     override fun onStart() {
        this.running = true

        /**
         * Server run cycle as a supplemental task
         */
        this.submitSupplementalTask {
            while (this.running) {
                this.server.get().run()
            }
        }
    }

    /**
     * Service run cycle
     */
    override fun run() {
        this.client.get().run()
    }

    override fun onStop(interrupted: Boolean) {
        super.onStop(interrupted)
        this.running = false
        this.client.ifSet { it.close() }
        this.client.reset()
        this.server.ifSet { it.close() }
        this.server.reset()
    }
}