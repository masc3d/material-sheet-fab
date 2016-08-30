package sx.net

import org.slf4j.LoggerFactory
import rx.lang.kotlin.PublishSubject
import rx.lang.kotlin.synchronized
import sx.Disposable
import sx.LazyInstance
import sx.concurrent.Service
import sx.io.serialization.KryoSerializer
import sx.io.serialization.Serializer
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
class UdpDiscoveryService<TInfo> @JvmOverloads constructor(
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
    class Host<TInfo> internal constructor(
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

        override fun hashCode(): Int {
            var result = info?.hashCode() ?: 0
            result = 31 * result + Arrays.hashCode(addressData)
            return result
        }

        override fun toString(): String {
            return "Host(address=${this.address}, info=$info)"
        }
    }

    /**
     * Network interfaces
     */
    private val interfaces = LazyInstance({
        NetworkInterface.getNetworkInterfaces().toList()
                .filter { !it.isLoopback && it.isUp }
                .flatMap { it.interfaceAddresses }
                .filter { !it.address.isLoopbackAddress && it.address.isSiteLocalAddress && it.broadcast != null }
    })

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
    private var info = LazyInstance<TInfo?>({ null })

    /**
     * Info blocks by address
     */
    private val infoByAddress = mutableMapOf<InetAddress, Host<TInfo>>()

    enum class UpdateEventType {
        Changed,
        Removed
    }

    inner class UpdateEvent(val type: UpdateEventType, val host: Host<TInfo>) {
        override fun toString(): String {
            return "UpdateEvent(type=$type, host=$host)"
        }
    }

    /**
     * Update event
     */
    val rxOnUpdate by lazy { rxOnUpdateSubject.asObservable() }
    private val rxOnUpdateSubject = PublishSubject<UpdateEvent>().synchronized()

    private fun updateHost(host: Host<TInfo>) {
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
        this.info.set({ info }, true)
        this.info.reset()
    }

    /**
     * Discovery server
     */
    @Suppress("UNCHECKED_CAST")
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

            // Deserialize packet
            val host = this@UdpDiscoveryService.serializer.deserializeFrom(
                    packet.data.copyOf(packet.data.size)) as Host<TInfo>

            // Update directory from requesting host
            this@UdpDiscoveryService.updateHost(host)

            // Send response for this host
            val response = this@UdpDiscoveryService.serializer.serializeToByteArray(
                    Host(InetSocketAddress(0).address, this@UdpDiscoveryService.info.get()))

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

        @Suppress("UNCHECKED_CAST")
        override fun run() {
            this.log.trace("Starting discovery client cycle")

            // Broadcast the message over all the network interfaces
            this@UdpDiscoveryService.interfaces.reset()
            val interfaces = this@UdpDiscoveryService.interfaces.get()
            interfaces.forEach {
                val broadcast = it.broadcast

                // Create host entry for this interface/address and update
                val host = Host(it.address, this@UdpDiscoveryService.info.get())
                this@UdpDiscoveryService.updateHost(host)

                val sendData = this@UdpDiscoveryService.serializer.serializeToByteArray(host)
                val sendPacket = DatagramPacket(sendData, sendData.size, broadcast, this@UdpDiscoveryService.port)
                this.log.info("Broadcasting to ${sendPacket.address}")
                this.socket.send(sendPacket)
            }

            this.log.info("Waiting for replies")

            val interfaceAddresses = interfaces.map { it.address }.toHashSet()

            // Receive packet in a loop until timeout
            val hostsByAddress = mutableMapOf<InetAddress, Host<TInfo>>()
            while (true) {
                val receivePacket = DatagramPacket(buffer, buffer.size)

                try {
                    this.socket.receive(receivePacket)

                    // Deserialize packet
                    var host = this@UdpDiscoveryService.serializer.deserializeFrom(
                            receivePacket.data.copyOf(receivePacket.length)) as Host<TInfo>

                    // If packet is a server reply, it won't contain an address
                    if (host.address.equals(InetSocketAddress(0).address)) {
                        // In this case complement the address from the received packet
                        host = Host(receivePacket.address, host.info)
                    }

                    if (!interfaceAddresses.contains(host.address)) {
                        // Only process replies which do not relate to/are not sent by this host
                        hostsByAddress[host.address] = host

                        // Update directory with received host info
                        this@UdpDiscoveryService.updateHost(host)
                    } else {
                        this.log.info("Ignoring reply for local host [${host.address}]")
                    }
                } catch(e: SocketTimeoutException) {
                    break
                }
            }

            // Post processing
            this@UdpDiscoveryService.lock.withLock {
                // Find all non-local addresses for which no replies have been received
                val removed = this@UdpDiscoveryService.infoByAddress.filter {
                    !interfaceAddresses.contains(it.key) && !hostsByAddress.containsKey(it.key)
                }

                // Remove them and notify
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