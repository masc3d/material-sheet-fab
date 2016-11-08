package sx.net

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.lang.kotlin.PublishSubject
import rx.lang.kotlin.synchronized
import sx.Disposable
import sx.LazyInstance
import sx.concurrent.Service
import sx.io.serialization.KryoSerializer
import sx.io.serialization.Serializable
import sx.io.serialization.Serializer
import java.net.*
import java.time.Duration
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Lightweight universal udp discovery service. Instances act as a discovery server and client at the same time
 * @param executorService Executor service to use
 * @property port Port to listen on
 * @param uid Discovery node unique id. Defaults to a random UUID string.
 * @param serverEnabled Enables listener for answering broadcast request. Defaults to true.
 * @param TInfo (Optional) Info block type.
 * The class must be serializable, should be immutable and implement `.equals`for change notifications.
 * A kotlin data class using only `val`s is a good match.
 * @param serializer (Optional) Serializer override
 * Created by masc on 29/08/16.
 */
open class UdpDiscoveryService<TInfo> @JvmOverloads constructor(
        executorService: ScheduledExecutorService,
        val port: Int,
        val uid: String = UUID.randomUUID().toString(),
        val serverEnabled: Boolean = true,
        infoClass: Class<TInfo>,
        private val serializer: Serializer = KryoSerializer())
:
        Service(
                executorService = executorService,
                period = Duration.ofSeconds(30)) {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private var running = false

    private val BUFFER_SIZE = 16 * 1024
    private val RECEIVE_TIMEOUT = Duration.ofSeconds(2)

    /** Node identification/key */
    data class NodeId(val uid: String, val address: InetAddress)

    /**
     * Node discovery info
     * @param uid Unique id of this node/host
     * @param address Host address
     * @param removed If this host shall be removed/is shutting down. Defaults ot false.
     * @property info Optional info block
     */
    @Serializable(0xe4d5af61ac4d1f)
    class Node<TInfo> internal constructor(
            val uid: String? = null,
            address: InetAddress? = null,
            val removed: Boolean = false,
            val info: TInfo? = null) {
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
            if (other == null || !(other is Node<*>))
                return false

            if (this.uid != other.uid)
                return false

            if (this.address != other.address)
                return false

            if (this.removed != other.removed)
                return false

            if (this.info == null && other.info == null)
                return true

            return (this.info != null && other.info != null && this.info == other.info)
        }

        override fun hashCode(): Int {
            var result = uid?.hashCode() ?: 0
            result = 31 * result + removed.hashCode()
            result = 31 * result + (info?.hashCode() ?: 0)
            result = 31 * result + Arrays.hashCode(addressData)
            return result
        }

        override fun toString(): String {
            return "Host(uid=${uid}, address=${this.address}, removed=${removed}, info=$info)"
        }
    }

    /**
     * Wrapper for releveant network interface/address info
     */
    private class InterfaceAddressInfo(
            val networkInterface: NetworkInterface,
            val interfaceAddress: InterfaceAddress) {
    }

    init {
        this.serializer.register(infoClass)
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
    val info: TInfo?
        get() = _info.get()

    private var _info = LazyInstance<TInfo?>({ null })

    /**
     * Internal directory of hosts/infos by address.
     * Access to this instance must be protected with `this.lock`
     */
    private val _directory = linkedMapOf<NodeId, Node<TInfo>>()

    /**
     * Returns a copy of the directory map
     */
    val directory: List<Node<TInfo>> get() = this.lock.withLock { _directory.values.toList() }

    /**
     * Notification event
     */
    class UpdateEvent<TInfo>(val type: Type, val node: Node<TInfo>) {
        enum class Type {
            Changed,
            Removed
        }

        override fun toString(): String {
            return "UpdateEvent(type=$type, host=$node)"
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
    private fun updateDirectory(node: Node<TInfo>, log: Logger) {
        var updated = true
        if (node.uid == null) {
            log.warn("UID of node [${node}] is null")
            return
        }

        val nodeId = NodeId(node.uid, node.address)

        this.lock.withLock {
            val existing = this._directory[nodeId]
            if (existing == null) {
                log.trace("No entry for node [${nodeId}]")
            }

            updated = (existing != node)
            if (updated) {
                log.info("Updating info for ${node}")
                this._directory[nodeId] = node
            }
        }

        if (updated) {
            this.rxOnUpdateSubject.onNext(UpdateEvent(UpdateEvent.Type.Changed, node))
        }
    }

    /**
     * Updates info for this host. Triggers a publish cycle
     */
    var nodeInfo: TInfo?
        get() {
            return _info.get()
        }
        set(value) {
            _info.resetIf({ it != value }, value)
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
            try {
                this.log.trace("Starting discovery host cycle")

                val packet = DatagramPacket(buffer, buffer.size)
                this.socket.receive(packet)

                // Deserialize packet
                @Suppress("UNCHECKED_CAST")
                val host = this@UdpDiscoveryService.serializer.deserializeFrom(
                        packet.data.copyOf(packet.data.size)) as Node<TInfo>

                if (!this@UdpDiscoveryService.interfaces.get().containsKey(packet.address)) {
                    log.debug("Received info from [${host.address}]")

                    // Update directory from requesting host
                    this@UdpDiscoveryService.updateDirectory(host, this.log)

                    // Send response for this host
                    val response = this@UdpDiscoveryService.serializer.serializeToByteArray(
                            Node(address = InetSocketAddress(0).address,
                                    uid = this@UdpDiscoveryService.uid,
                                    info = this@UdpDiscoveryService.info))

                    log.debug("Answering to ${packet.address} size [${response.size}]")

                    val responsePacket = DatagramPacket(response, response.size, packet.address, packet.port)
                    this.socket.send(responsePacket)
                }
            } catch(e: SocketException) {
                // Don't throw/report when socket was closed (eg. shutdown situation)
                if (!this.socket.isClosed)
                    throw e
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

        fun broadcast() {
            // Broadcast the message over all the network interfaces
            this@UdpDiscoveryService.interfaces.reset()
            val interfaceAddresses = this@UdpDiscoveryService.interfaces.get()
            interfaceAddresses.values.forEach {
                val broadcast = it.interfaceAddress.broadcast

                // Create host entry for this interface/address
                val host = Node(
                        address = it.interfaceAddress.address,
                        uid = this@UdpDiscoveryService.uid,
                        removed = !this@UdpDiscoveryService.running,
                        info = this@UdpDiscoveryService.info)

                // Update directory with received host info
                this@UdpDiscoveryService.updateDirectory(host, this.log)

                val sendData = this@UdpDiscoveryService.serializer.serializeToByteArray(host)
                val sendPacket = DatagramPacket(sendData, sendData.size, broadcast, this@UdpDiscoveryService.port)
                this.log.debug("Broadcasting to ${sendPacket.address}")
                this.socket.send(sendPacket)
            }
        }

        override fun run() {
            try {
                this.log.trace("Starting discovery client cycle")

                this.broadcast()

                val interfaceAddresses = this@UdpDiscoveryService.interfaces.get()

                this.log.trace("Processing replies")

                // Receive packet in a loop until timeout
                val nodesById = mutableMapOf<NodeId, Node<TInfo>>()
                while (true) {
                    val receivePacket = DatagramPacket(buffer, buffer.size)

                    try {
                        this.socket.receive(receivePacket)

                        // Deserialize packet
                        @Suppress("UNCHECKED_CAST")
                        var node = this@UdpDiscoveryService.serializer.deserializeFrom(
                                receivePacket.data.copyOf(receivePacket.length)) as Node<TInfo>

                        // If packet is a server reply, it won't contain an address
                        if (node.address == InetSocketAddress(0).address) {
                            // In this case complement the address from the received packet
                            node = Node(
                                    address = receivePacket.address,
                                    uid = node.uid,
                                    removed = node.removed,
                                    info = node.info)
                        }

                        if (!interfaceAddresses.containsKey(node.address)) {
                            if (node.uid != null) {
                                val nodeId = NodeId(node.uid!!, node.address)
                                // Only process replies which do not relate to/are not sent by this host
                                nodesById[nodeId] = node

                                // Update directory with received host info
                                this@UdpDiscoveryService.updateDirectory(node, this.log)
                            } else {
                                log.warn("Ignoring node [${node}], uid is null")
                            }
                        } else {
                            this.log.trace("Ignoring reply for local host [${node.address}]")
                        }
                    } catch(e: SocketTimeoutException) {
                        break
                    }
                }

                // Post processing
                this@UdpDiscoveryService.lock.withLock {
                    // Find all non-local addresses for which no replies have been received
                    val removed = this@UdpDiscoveryService._directory.filter {
                        val node = nodesById[it.key]

                        // Either no info has been received for a non-local address
                        (node == null && !interfaceAddresses.containsKey(it.value.address))
                        // or the node has been activelx removed
                                || (node != null && node.removed)
                    }

                    // Remove them and notify
                    removed.forEach {
                        log.info("Removing entry for ${it.key}")
                        this@UdpDiscoveryService._directory.remove(it.key)
                        this@UdpDiscoveryService.rxOnUpdateSubject.onNext(UpdateEvent(UpdateEvent.Type.Removed, it.value))
                    }
                }
            } catch(e: SocketException) {
                // Don't throw/report when socket was closed (eg. shutdown situation)
                if (!this.socket.isClosed)
                    throw e
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

        if (this.serverEnabled) {
            /**
             * Server run cycle as a supplemental task
             */
            this.submitSupplementalTask {
                while (this.running) {
                    try {
                        this.server.get().run()
                    } catch(e: BindException) {
                        log.error(e.message, e)
                        log.warn("Could not bind server, falling back to client only mode")
                        break
                    } catch(e: Exception) {
                        log.error(e.message, e)
                        // Prevent CPU hogging on unexpected exceptions
                        Thread.sleep(2000)
                    }
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
        this.client.get().broadcast()
        this.client.ifSet { it.close() }
        this.client.reset()
        this.server.ifSet { it.close() }
        this.server.reset()
    }
}
