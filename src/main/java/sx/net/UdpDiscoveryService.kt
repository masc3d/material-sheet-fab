package sx.net

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.lang.kotlin.PublishSubject
import rx.lang.kotlin.synchronized
import sx.Copyable
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
 * @param TInfo (Optional) Info block type. The class must be serializable, should be immutable and implement `.equals` for change notifications
 * @param serializer (Optional) Serializer override
 * Created by masc on 29/08/16.
 */
class UdpDiscoveryService<TInfo> @JvmOverloads constructor(
        val port: Int,
        private val serializer: Serializer = KryoSerializer())
:
        Service(
                executorService = Executors.newScheduledThreadPool(2),
                period = Duration.ofSeconds(30)) where TInfo : Copyable<TInfo>, TInfo : Serializable {
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
            return "Host(address=${this.address},, info=$info)"
        }
    }

    /**
     * Wrapper for releveant network interface/address info
     */
    private class InterfaceAddressInfo(
            val networkInterface: NetworkInterface,
            val interfaceAddress: InterfaceAddress) {
    }

    /**
     * Network interfaces
     */
    private val interfaces = LazyInstance<Map<InetAddress, InterfaceAddressInfo>>({
        mapOf(
                *NetworkInterface.getNetworkInterfaces().toList()
                        // Filter interfaces
                        .filter { !it.isLoopback && it.isUp }
                        // Map relevant information to InterfaceAddressInfo
                        .flatMap { it.interfaceAddresses.map { ia -> InterfaceAddressInfo(it, ia) } }
                        // Filter by address criteria
                        .filter {
                            !it.interfaceAddress.address.isLoopbackAddress &&
                                    it.interfaceAddress.address.isSiteLocalAddress &&
                                    it.interfaceAddress.broadcast != null
                        }
                        // Convert to array of map pairs
                        .map { Pair(it.interfaceAddress.address, it) }
                        .toTypedArray())
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
     * Internal directory of hosts/infos by address.
     * Access to this instance must be protected with `this.lock`
     */
    private val _directory = mutableMapOf<InetAddress, Host<TInfo>>()

    /**
     * Returns a copy of the directory map
     */
    val directory: List<Host<TInfo>> get() = this.lock.withLock { _directory.values.toList() }

    /**
     * Notification event
     */
    class UpdateEvent<TInfo>(val type: Type, val host: Host<TInfo>) {
        enum class Type {
            Changed,
            Removed
        }

        override fun toString(): String {
            return "UpdateEvent(type=$type, host=$host)"
        }
    }

    /**
     * Update event
     */
    val rxOnUpdate by lazy { rxOnUpdateSubject.asObservable() }
    private val rxOnUpdateSubject = PublishSubject<UpdateEvent<TInfo>>().synchronized()

    /**
     * Updates a directory entry, notifying on change
     */
    private fun updateDirectory(host: Host<TInfo>, log: Logger) {
        var updated = true
        this.lock.withLock {
            updated = (this._directory[host.address] != host)
            if (updated) {
                log.info("Updated info for ${host}")
                this._directory[host.address] = host
            }
        }

        if (updated) {
            this.rxOnUpdateSubject.onNext(UpdateEvent(UpdateEvent.Type.Changed, host))
        }
    }

    /**
     * Updates info for this host. Triggers a publish cycle
     */
    fun updateInfo(info: TInfo?) {
        val i = info?.copyInstance()

        this.info.resetIf({ it != i }, i)

        this.trigger()
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

            // Deserialize packet
            @Suppress("UNCHECKED_CAST")
            val host = this@UdpDiscoveryService.serializer.deserializeFrom(
                    packet.data.copyOf(packet.data.size)) as Host<TInfo>

            if (!this@UdpDiscoveryService.interfaces.get().containsKey(packet.address)) {
                log.debug("Received info from [${host.address}]")

                // Update directory from requesting host
                this@UdpDiscoveryService.updateDirectory(host, this.log)

                // Send response for this host
                val response = this@UdpDiscoveryService.serializer.serializeToByteArray(
                        Host(address = InetSocketAddress(0).address,
                                info = this@UdpDiscoveryService.info.get()))

                log.debug("Answering to ${packet.address} size [${response.size}]")

                val responsePacket = DatagramPacket(response, response.size, packet.address, packet.port)
                this.socket.send(responsePacket)
            }
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
            this@UdpDiscoveryService.interfaces.reset()
            val interfaceAddresses = this@UdpDiscoveryService.interfaces.get()
            interfaceAddresses.values.forEach {
                val broadcast = it.interfaceAddress.broadcast

                // Create host entry for this interface/address
                val host = Host(
                        address = it.interfaceAddress.address,
                        info = this@UdpDiscoveryService.info.get())

                // Update directory with received host info
                this@UdpDiscoveryService.updateDirectory(host, this.log)

                val sendData = this@UdpDiscoveryService.serializer.serializeToByteArray(host)
                val sendPacket = DatagramPacket(sendData, sendData.size, broadcast, this@UdpDiscoveryService.port)
                this.log.info("Broadcasting to ${sendPacket.address}")
                this.socket.send(sendPacket)
            }

            this.log.trace("Processing replies")

            // Receive packet in a loop until timeout
            val hostsByAddress = mutableMapOf<InetAddress, Host<TInfo>>()
            while (true) {
                val receivePacket = DatagramPacket(buffer, buffer.size)

                try {
                    this.socket.receive(receivePacket)

                    // Deserialize packet
                    @Suppress("UNCHECKED_CAST")
                    var host = this@UdpDiscoveryService.serializer.deserializeFrom(
                            receivePacket.data.copyOf(receivePacket.length)) as Host<TInfo>

                    // If packet is a server reply, it won't contain an address
                    if (host.address.equals(InetSocketAddress(0).address)) {
                        // In this case complement the address from the received packet
                        host = Host(
                                address = receivePacket.address,
                                info = host.info)
                    }

                    if (!interfaceAddresses.containsKey(host.address)) {
                        // Only process replies which do not relate to/are not sent by this host
                        hostsByAddress[host.address] = host

                        // Update directory with received host info
                        this@UdpDiscoveryService.updateDirectory(host, this.log)
                    } else {
                        this.log.trace("Ignoring reply for local host [${host.address}]")
                    }
                } catch(e: SocketTimeoutException) {
                    break
                }
            }

            // Post processing
            this@UdpDiscoveryService.lock.withLock {
                // Find all non-local addresses for which no replies have been received
                val removed = this@UdpDiscoveryService._directory.filter {
                    !interfaceAddresses.containsKey(it.key) && !hostsByAddress.containsKey(it.key)
                }

                // Remove them and notify
                removed.forEach {
                    this@UdpDiscoveryService._directory.remove(it.key)
                    this@UdpDiscoveryService.rxOnUpdateSubject.onNext(UpdateEvent(UpdateEvent.Type.Removed, it.value))
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
                try {
                    this.server.get().run()
                } catch(e: Exception) {
                    log.error(e.message, e)
                }
            }
        }
    }

    /**
     * Service run cycle
     */
    override fun run() {
        this.client.get().run()
    }

    /**
     * On service stop
     */
    override fun onStop(interrupted: Boolean) {
        super.onStop(interrupted)
        this.running = false
        this.client.ifSet { it.close() }
        this.client.reset()
        this.server.ifSet { it.close() }
        this.server.reset()
    }
}