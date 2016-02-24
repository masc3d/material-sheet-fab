package sx.jms.embedded

import org.apache.commons.logging.LogFactory
import sx.Disposable
import sx.event.EventDelegate
import sx.event.EventDispatcher
import java.io.File
import java.util.*
import javax.jms.ConnectionFactory
import javax.jms.Queue
import javax.jms.Topic

/**
 * Created by masc on 01.06.15.
 */
abstract class Broker
/**
 * c'tor for derived classes to provide defaults
 * @param nativeTcpPort Native tcp port for this broker to listen to
 */
protected constructor(
        var nativeTcpPort: Int?)
:
        Disposable {
    /** Log  */
    protected var log = LogFactory.getLog(this.javaClass)

    /**
     * Transport type
     */
    enum class TransportType private constructor(private val mTransportType: String) {
        HTTP("http"),
        TCP("tcp");

        override fun toString(): String {
            return mTransportType
        }
    }

    /**
     * Broker user
     */
    class User(
            val userName: String,
            val password: String,
            val groupName: String)

    /**
     * Peer broker
     */
    class PeerBroker @JvmOverloads constructor(
            val hostname: String,
            val transportType: TransportType,
            val port: Int?,
            val httpPath: String? = null) {

        init {
            if (transportType != TransportType.HTTP && httpPath != null) {
                throw IllegalArgumentException("Http path not valid for non-http transports")
            }
        }
    }

    /**
     * Broker name
     */
    var brokerName: String = "localhost"

    /** Peer brokers  */
    protected val peerBrokers = ArrayList<PeerBroker>()

    /** Broker user */
    var user: User? = null

    /** Data directory for store  */
    var dataDirectory: File? = null

    //region Events
    /**
     * Broker event listener interface
     */
    interface EventListener : sx.event.EventListener {
        fun onStart() {
        }

        fun onStop() {
        }

        fun onConnectedToBrokerNetwork() {
        }

        fun onDisconnectedFromBrokerNetwork() {
        }
    }

    abstract class DefaultEventListener : EventListener {
        override fun onStart() {
        }

        override fun onStop() {
        }

        override fun onConnectedToBrokerNetwork() {
        }

        override fun onDisconnectedFromBrokerNetwork() {
        }
    }

    /** Broker event dispatcher/delegate  */
    protected var listenerEventDispatcher = EventDispatcher.createThreadSafe<EventListener>()
    val delegate: EventDelegate<EventListener> get() = listenerEventDispatcher
    //endregion

    //region Interface
    @Throws(Exception::class)
    protected abstract fun startImpl()

    @Throws(Exception::class)
    protected abstract fun stopImpl()

    protected abstract val isStartedImpl: Boolean

    /** Create jms message queue  */
    abstract fun createQueue(name: String): Queue

    /** Create jms message topic  */
    abstract fun createTopic(name: String): Topic

    /** Jms connection factory  */
    abstract val connectionFactory: ConnectionFactory
    //endregion

    /**
     * Start broker
     * @throws Exception
     */
    @Synchronized @Throws(Exception::class)
    fun start() {
        this.startImpl()
        listenerEventDispatcher.emit { listener -> listener.onStart() }
    }

    /**
     * Stop broker
     * @throws Exception
     */
    @Synchronized @Throws(Exception::class)
    fun stop() {
        if (this.isStarted) {
            listenerEventDispatcher.emit { listener -> listener.onStop() }
        }
        this.stopImpl()
    }

    val isStarted: Boolean
        get() = this.isStartedImpl

    /**
     * Add peer (network transport) broker
     * @param peerBroker Peer broker
     */
    fun addPeerBroker(peerBroker: PeerBroker) {
        peerBrokers.add(peerBroker)
    }

    override fun close() {
        try {
            this.stop()
        } catch (e: Exception) {
            log.error(e.message, e)
        }

    }
}
