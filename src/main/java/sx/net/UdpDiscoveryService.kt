package sx.net

import org.slf4j.LoggerFactory
import rx.lang.kotlin.PublishSubject
import rx.lang.kotlin.synchronized
import sx.Disposable
import sx.LazyInstance
import sx.concurrent.Service
import sx.io.serialization.*
import java.io.Serializable
import java.net.*
import java.time.Duration
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Lightweight universal udp discovery service
 * @property port Port to listen on
 * @param TInfo (Optional) Info block type. The class must be serializable and should implement `.equals` for change notifications
 * @param serializer (Optional) Serializer override
 * Created by masc on 29/08/16.
 */
class UdpDiscoveryService<TInfo> @JvmOverloads constructor (
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
     * @param address Host address
     * @property info Optional info block
     */
     class Host<TInfo> internal constructor (
            address: InetAddress? = null,
            val info: TInfo? = null) : Serializable {

        /**
         * Address data (as InetAddress is not universally serializable)
         */
        private val addressData: ByteArray

        /**
         * c'tor
         */
        init {
            this.addressData = address?.address ?: ByteArray(0)
        }

        /**
         * Internet address
         */
        @delegate:Transient
        val address by lazy { InetAddress.getByAddress(this.addressData) }

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

        override fun toString(): String{
            return "Host(address=${this.address}, info=$info)"
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
     * Lock for accessing shared resources
     */
    private val lock = ReentrantLock()

    /**
     * Info block for this host
     */
    private var info: TInfo? = null

    /**
     * Info blocks by address
     */
    private val infoByAddress = mutableMapOf<InetAddress, Host<TInfo>>()

    enum class UpdateEventType {
        Changed,
        Removed
    }

    inner class UpdateEvent(val type: UpdateEventType, val host: Host<TInfo>) {
        override fun toString(): String{
            return "UpdateEvent(type=$type, host=$host)"
        }
    }

    /**
     * Update event
     */
    val rxOnUpdate by lazy { rxOnUpdateSubject.asObservable() }
    private val rxOnUpdateSubject = PublishSubject<UpdateEvent>().synchronized()

    /**
     * Can be overridden to create a customized response message
     * @param address Address this response is going to be sent to
     * @param request Request data
     */
    private fun createPacket(address: InetAddress): ByteArray {
        val host = Host(address, this.info)
        return this.serializer.serializeToByteArray(host)
    }

    /**
     * Called when a response is received
     * @param address Address of sender
     * @param response Response data
     */
    @Suppress("UNCHECKED_CAST")
    private fun parsePacket(response: ByteArray): Host<TInfo> {
        val host = this.serializer.deserializeFrom(response) as Host<TInfo>
        return host
    }

    private fun onInfo(host: Host<TInfo>) {
        this.log.info("Discovered ${host}")

        var updated = true
        this.lock.withLock {
            val info = this.infoByAddress[host.address]
            if (info != null) {
                updated = info.equals(host.info)
            }
            if (updated) {
                this.infoByAddress[host.address] = host
            }
        }

        if (updated) {
            this.rxOnUpdateSubject.onNext(UpdateEvent(UpdateEventType.Changed, host))
        }
    }

    /**
     * Updates info for this host
     */
    fun updateInfo(info: TInfo?) {
        lock.withLock {
            this.info = info
        }
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

            val packet = DatagramPacket(buffer, buffer.size)
            this.socket.receive(packet)

            val host = this@UdpDiscoveryService.parsePacket(packet.data.copyOf(packet.data.size))

            this@UdpDiscoveryService.onInfo(host)
            val response = this@UdpDiscoveryService.createPacket(InetSocketAddress(0).address)

            log.debug("Answering to ${packet.address} size [${response.size}]")

            val responsePacket = DatagramPacket(response, response.size, packet.address, packet.port)
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

            val hostsByAddress = mutableMapOf<InetAddress, Host<TInfo>>()
            while (true) {
                val receivePacket = DatagramPacket(buffer, buffer.size)

                try {
                    this.socket.receive(receivePacket)
                    var host = this@UdpDiscoveryService.parsePacket(receivePacket.data.copyOf(receivePacket.length))
                    if (host.address.equals(InetSocketAddress(0).address)) {
                        this.log.debug("ZERO")
                        host = Host(receivePacket.address, host.info)
                    }
                    if (hostsByAddress.contains(host.address)) {
                        this.log.info("Discarding previous reply from [${host.address}]")
                    }
                    hostsByAddress[host.address] = host
                } catch(e: SocketTimeoutException) {
                    break
                }
            }

            hostsByAddress.forEach {
                this@UdpDiscoveryService.onInfo(it.value)
            }

            this@UdpDiscoveryService.lock.withLock {
                val removed = this@UdpDiscoveryService.infoByAddress.filter { !hostsByAddress.containsKey(it.key) }
                removed.forEach {
                    this@UdpDiscoveryService.infoByAddress.remove(it.key)
                    this@UdpDiscoveryService.rxOnUpdateSubject.onNext(UpdateEvent(UpdateEventType.Removed, it.value))
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