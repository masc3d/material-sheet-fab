package sx.jms

import org.slf4j.LoggerFactory
import sx.Action
import sx.Disposable
import sx.LazyInstance
import java.io.Closeable
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeoutException
import java.util.function.Supplier
import javax.jms.*

/**
 * Lightweight messaging channel with send/receive and automatic message conversion capabilities.
 * Caches session and message consumer/producer.
 * TODO: consider seperating channel configuration from actual implementation, as eg. listeners have their dedicated session transaction setting, so it may be slightly confusing
 * TODO: add support for actively supporting correlation id for reusing temporary queues/channels in a request/response scheme
 * TODO: add support for temporary queue pooling, to prevent creation of temporary queues per request (sendRequest) @link TemporaryConnectionPool
 * Created by masc on 06.07.15.
 * @param configuration Channel configuration
 * @param session Optional: jms session to use
 */
class Channel @JvmOverloads constructor(
        configuration: Configuration,
        session: Session? = null)
:
        Disposable,
        Closeable {

    /**
     * c'tor for creating channel using a new connection created via connection factory
     * @param connectionFactory JMS connection factory
     * @param sessionTransacted Dedicated session for this channel should be transacted. Defaults to true
     * @param destination JMS destination
     * @param converter Message converter
     * @param deliveryMode JMS delivery mode (eg. persistent/non-persistent)
     */
    @JvmOverloads constructor(connectionFactory: ConnectionFactory,
                              sessionTransacted: Boolean = Channel.JMS_TRANSACTED,
                              destination: Destination,
                              converter: Converter,
                              deliveryMode: Channel.DeliveryMode = Channel.JMS_DELIVERY_MODE
    ) : this(
            configuration = Configuration(connectionFactory = connectionFactory,
                    sessionTransacted = sessionTransacted,
                    destination = destination,
                    converter = converter,
                    deliveryMode = deliveryMode),
            session = null) {
    }

    /**
     * c'tor for creating channel using an existing session
     * @param session JMS sesssion. Optional, if omitted a dedicated session will be created for this channel.
     * @param destination JMS destination
     * @param converter Message converter
     * @param deliveryMode JMS delivery mode (eg. persistent/non-persistent)
     */
    @JvmOverloads constructor(session: Session,
                              destination: Destination,
                              converter: Converter,
                              deliveryMode: Channel.DeliveryMode = Channel.JMS_DELIVERY_MODE) : this(
            configuration = Configuration(
                    connectionFactory = null,
                    sessionTransacted = session.transacted,
                    destination = destination,
                    converter = converter,
                    deliveryMode = deliveryMode),
            session = session) {
    }

    companion object {
        private val RECEIVE_TIMEOUT = Duration.ofSeconds(10)
        private val JMS_TTL = Duration.ZERO
        private val JMS_TRANSACTED = true
        private val JMS_DELIVERY_MODE = Channel.DeliveryMode.NonPersistent

        // @see TemporaryQueuePool
        // val temporaryQueuePool = TemporaryQueuePool()
    }

    /**
     * Delivery modes
     */
    enum class DeliveryMode(val value: Int) {
        NonPersistent(javax.jms.DeliveryMode.NON_PERSISTENT),
        Persistent(javax.jms.DeliveryMode.PERSISTENT)
    }

    // TODO: currently unused, this needs more work as temporary queues cannot be shared across connections
    // As connections may be pooled by the factory itself, it will become tricky to impossible to track
    // connections, as this process is intransparent via jms API.
    // To make this work properly, will have to implement our own connection pool we have
    // control over, which may or should also support threadlocal pooling, replacing connection factory
    // itself as a connection provider for all sx.jms classes
    class TemporaryQueuePool {
        private val log = LoggerFactory.getLogger(this.javaClass)
        private val queue = HashMap<String, LinkedList<TemporaryQueue>>()

        private fun resolveKey(connection: Connection): String {
            return connection.toString()
        }

        fun get(connection: Connection, session: Session): PooledTemporaryQueue {
            synchronized(queue) {
                val key = this.resolveKey(connection)
                var list = queue.get(key)
                if (list == null) {
                    list = LinkedList<TemporaryQueue>()
                    queue.put(key, list)
                }

                val temporaryQueue: TemporaryQueue
                if (list.count() > 0) {
                    temporaryQueue = list.poll()
                    log.info("Polled from queue [${temporaryQueue}")
                } else {
                    temporaryQueue = session.createTemporaryQueue()
                    log.info("Created queue [${temporaryQueue}")
                }

                return PooledTemporaryQueue(key, temporaryQueue, this)
            }
        }

        fun release(t: PooledTemporaryQueue) {
            synchronized(queue) {
                var list = queue.get(t.key)
                if (list == null)
                    throw IllegalArgumentException("Unknown key")

                log.info("Releasing queue [${t.temporaryQueue}")
                if (!list.contains(t.temporaryQueue))
                    list.offer(t.temporaryQueue)
            }
        }
    }

    /**
     * Pooled temporary queue
     */
    class PooledTemporaryQueue(val key: String,
                               val temporaryQueue: TemporaryQueue,
                               val pool: TemporaryQueuePool)
    :
            TemporaryQueue by temporaryQueue {
        override fun delete() {
            pool.release(this)
        }
    }

    /**
     * Channel configuration
     * All common channel configuration settings are grouped into this shallow structure which
     * can be easily (and automatically) replicated
     * @param connectionFactory Connection factory
     * @param sessionTransacted Sessions created through connection factory should be transacted
     * @param destination Destination
     * @param converter Message converter
     * @param deliveryMode JMS delivery mode
     */
    class Configuration @JvmOverloads constructor (val connectionFactory: ConnectionFactory?,
                                                   sessionTransacted: Boolean = Channel.JMS_TRANSACTED,
                                                   destination: Destination,
                                                   val converter: Converter,
                                                   deliveryMode: Channel.DeliveryMode = Channel.JMS_DELIVERY_MODE)
    :
            Cloneable {

        var destination: Destination
            private set

        var deliveryMode: DeliveryMode
            private set

        var sessionTransacted: Boolean
            private set

        init {
            this.destination = destination
            this.deliveryMode = deliveryMode
            this.sessionTransacted = sessionTransacted
        }

        /** Time to live for messages sent through this channel */
        var ttl: Duration = Channel.JMS_TTL
        /** Priority for messages sent through this channel */
        var priority: Int? = null
        /** Auto-commit messages on send, defaults to true */
        var autoCommit: Boolean = true
        /** Default receive timeout for receive/sendReceive calls. Defaults to 10 seconds. */
        var receiveTimeout: Duration = Channel.RECEIVE_TIMEOUT

        /**
         * Clone channel configuration, optionally overriding properties
         * @param sessionTransacted Override session transaction mode
         * @param destination Override destination
         * @param deliveryMode Override delivery mode
         * @return Cloned configuration
         */
        fun clone(
                sessionTransacted: Boolean? = null,
                destination: Destination? = null,
                deliveryMode: Channel.DeliveryMode? = null
        ): Configuration {

            // Clone configuration field by field
            val newChannel = this.clone() as Configuration
            // Override settings
            if (sessionTransacted != null)
                newChannel.sessionTransacted = sessionTransacted
            if (destination != null)
                newChannel.destination = destination
            if (deliveryMode != null)
                newChannel.deliveryMode = deliveryMode

            return newChannel
        }

        override fun toString(): String {
            return "Destination [${this.destination}]"
        }
    }

    /**
     * Channel statistics
     * Due to a restriction in jms, message sizes can only be determined when using a
     * converter implementation which supports it. Sizes of pure jms messages can usually not be determined reliably.
     */
    class Statistics {
        var bytesSent: Long = 0
            private set

        var bytesReceived: Long = 0
            private set

        var enabled: Boolean = false

        fun reset() {
            bytesSent = 0
            bytesReceived = 0
        }

        /**
         * Callback to account sent bytes, to be provided to a converter
         */
        val accountSent: ((size: Long) -> Unit)?
            get() {
                if (!this.enabled)
                    return null
                return { size -> bytesSent += size }
            }

        /**
         * Callback to account received bytes, to be provided to a converter
         */
        val accountReceived: ((size: Long) -> Unit)?
            get() {
                if (!this.enabled)
                    return null

                return { size -> bytesReceived += size }
            }

    }

    private val log = LoggerFactory.getLogger(this.javaClass)
    val configuration: Configuration

    /**
     * Lazy connection instance
     */
    private val connection = LazyInstance<Connection>(
            LazyInstance.ThreadSafeMode.None)

    /**
     * Lazy session instance
     */
    private val sessionInstance = LazyInstance<Session>(
            LazyInstance.ThreadSafeMode.None)

    /**
     * Lazy consumer instance
     */
    private val consumer = LazyInstance<MessageConsumer>(
            LazyInstance.ThreadSafeMode.None)

    /**
     * Lazy producer instance
     */
    private val producer = LazyInstance<MessageProducer>(
            LazyInstance.ThreadSafeMode.None)

    /**
     * Indicates if this channel owns the session. If true, session will be properly closed with the channel
     */
    private var ownsSession = false

    /**
     * Indicates if this channel owns this destination, eg. when creating a response channel without explcitly providing
     * a destination. In this case the destination will be deleted when the channel is closed.
     */
    private var ownsDestination = false

    /**
     * Indicates if this channels owns the connection, thus has to close it accordingly
     */
    private var ownsConnection = false

    val connectionFactory: ConnectionFactory?
        get() = this.configuration.connectionFactory

    val sessionTransacted: Boolean
        get() = this.configuration.sessionTransacted

    val session: Session
        get() = this.sessionInstance.get()

    val destination: Destination
        get() = this.configuration.destination

    val converter: Converter
        get() = this.configuration.converter

    val deliveryMode: DeliveryMode
        get() = this.configuration.deliveryMode

    var ttl: Duration
        get() = this.configuration.ttl
        set(value) {
            this.configuration.ttl = value
        }

    var priority: Int?
        get() = this.configuration.priority
        set(value) {
            this.configuration.priority = value
        }

    var autoCommit: Boolean
        get() = this.configuration.autoCommit
        set(value) {
            this.configuration.autoCommit = value
        }

    var receiveTimeout: Duration
        get() = this.configuration.receiveTimeout
        set(value) {
            this.configuration.receiveTimeout = value
        }

    /**
     * Channel statistics
     */
    val statistics: Statistics = Statistics()

    /**
     * init
     */
    init {
        this.configuration = configuration
        // Initialize lazy properties

        // JMS connection
        this.connection.set(Supplier {
            val cnf = this.connectionFactory
            if (cnf == null)
                throw IllegalStateException("Channel does not have connection factory")

            val cn = cnf.createConnection()
            cn.start()

            this.ownsConnection = true

            cn
        })

        // JMS session
        this.sessionInstance.set(Supplier {
            var finalSession: Session
            // Return c'tor provided session if applicable
            if (session != null) {
                finalSession = session
            } else {
                // Create session
                this.ownsSession = true
                finalSession = connection.get().createSession(this.sessionTransacted, Session.AUTO_ACKNOWLEDGE)
            }
            finalSession
        })

        // JMS consumer
        this.consumer.set(Supplier {
            this.sessionInstance.get().createConsumer(destination)
        })

        // JMS producer
        this.producer.set(Supplier {
            this.sessionInstance.get().createProducer(destination)
        })
    }

    /**
     * Create temporary queue channel replicating this channel's basic properties
     * @param session: Optional session: If not provided this channel's session will be used
     * @param destination: Optinoal destination. If not provided an internal one will be created and destroyed on channel close
     * @return Temporary queue channel
     */
    private fun createReplyChannelInternal(session: Session = this.sessionInstance.get(), destination: Destination? = null): Channel {
        val replyChannel: Channel
        var replySession: Session? = null
        if (!session.transacted) {
            // Session is not transacted, simply reuse it
            replySession = session
        } else {
            if (this.connectionFactory == null)
                throw java.lang.IllegalStateException("Cannot create response channel from transacted channel without connection factory")
        }
        val replyDestination = destination ?:
                session.createTemporaryQueue()

        replyChannel = Channel(
                configuration = this.configuration.clone(
                        // Temporary queue channels should not be transacted.
                        // EG. ActiveMQ has a problem with temporary destinations and transacted sessions, resulting in wrong message count
                        // and (non-fatal) messages about pending queue entries when deleting transacted temporary queues
                        sessionTransacted = false,
                        destination = replyDestination,
                        // Reply messages are never persistent
                        deliveryMode = DeliveryMode.NonPersistent),
                session = replySession)

        if (destination == null) {
            replyChannel.ownsDestination = true
            replyChannel.connection.set({ this.connection.get() })
        }

        return replyChannel
    }

    /**
     * Create reply channel
     * @param session Session
     * @param destination Destination
     */
    fun createReplyChannel(session: Session, destination: Destination): Channel {
        return this.createReplyChannelInternal(session, destination)
    }

    /**
     * Create reply channel
     */
    fun createReplyChannel(): Channel {
        return this.createReplyChannelInternal(destination = null)
    }

    /**
     * Send jms message
     * @param jmsMessage Message to send
     * @param messageConfigurer Callback for customizing the message before sending
     */
    @JvmOverloads fun send(jmsMessage: Message, messageConfigurer: Action<Message>? = null) {
        val mp = this.producer.get()

        mp.deliveryMode = deliveryMode.value
        mp.timeToLive = ttl.toMillis()
        val priority = priority
        if (priority != null)
            mp.priority = priority

        // Customize message if applicable
        messageConfigurer?.perform(jmsMessage)

        // Send actual message
        mp.send(destination, jmsMessage)

        if (this.autoCommit)
            this.commit()
    }

    /**
     * Sends jms message as a request, attaching a replyTo queue
     * @param jmsMessage Message to send
     * @param messageConfigurer Callback for customizing the message before sending
     */
    @JvmOverloads fun sendRequest(jmsMessage: Message, replyChannel: Channel = this.createReplyChannelInternal(), messageConfigurer: Action<Message>? = null): Channel {
        this.send(jmsMessage, Action {
            messageConfigurer?.perform(it)
            it.jmsReplyTo = replyChannel.destination
        })

        return replyChannel
    }

    /**
     * Send object as message using converter
     * @param message
     * @param messageConfigurer Callback for customizing the message before sending
     */
    @JvmOverloads fun send(message: Any, messageConfigurer: Action<Message>? = null) {
        this.send(
                this.converter.toMessage(
                        obj = message,
                        session = sessionInstance.get(),
                        onSize = statistics.accountSent),
                messageConfigurer)
    }

    /**
     * Sends message as a request, attaching a replyTo queue
     * Creates a temporary queue and referring channel and returns it for receiving the response
     * @param message Message to send
     * @param messageConfigurer Optional callback to customize jms message
     * @return Response/reply channel
     */
    @JvmOverloads fun sendRequest(message: Any, replyChannel: Channel = this.createReplyChannelInternal(), messageConfigurer: Action<Message>? = null): Channel {
        return this.sendRequest(
                this.converter.toMessage(
                        obj = message,
                        session = sessionInstance.get(),
                        onSize = statistics.accountSent),
                replyChannel,
                messageConfigurer)
    }

    /**
     * Receive message as object using converter
     * @param messageType Type of message
     */
    @Throws(TimeoutException::class)
    fun <T> receive(messageType: Class<T>): T {
        return this.receive(this.consumer.get(), messageType)
    }

    /**
     * Receive jms message
     */
    fun receive(): Message? {
        return this.receive(this.consumer.get())
    }

    /**
     * Receive message
     * @return Message
     */
    private fun <T> receive(consumer: MessageConsumer, messageType: Class<T>): T {
        val jmsMessage = this.receive(consumer)

        if (jmsMessage == null)
            throw TimeoutException("Timeout while waiting for message [${messageType.simpleName}]")

        return messageType.cast(
                this.converter.fromMessage(
                        message = jmsMessage,
                        onSize = this.statistics.accountReceived))
    }

    /**
     * Receive jms message.
     * @return Jms message or null on timeout
     */
    private fun receive(consumer: MessageConsumer): Message? {
        return if (this.receiveTimeout == Duration.ZERO)
            consumer.receive()
        else
            consumer.receive(this.receiveTimeout.toMillis())
    }

    /**
     * Explicitly commit transaction if the session is transacted.
     * If the session is not transacted invoking this method has no effect.
     */
    fun commit() {
        val session = sessionInstance.get()
        if (session.transacted)
            session.commit()
    }

    /**
     * Close channel
     */
    override fun close() {
        // Close message consumer
        consumer.ifSet(Action { c ->
            try {
                c.close()
            } catch (e: Exception) {
                log.error(e.message, e)
            }
        })
        consumer.reset()

        // Close message producer
        producer.ifSet(Action { p ->
            try {
                p.close()
            } catch (e: Exception) {
                log.error(e.message, e)
            }
        })
        producer.reset()

        // Commit session (must occur after closing consumer(s))
        sessionInstance.ifSet({
            try {
                this.commit()
            } catch (e: Exception) {
                log.error(e.message, e)
            }
        })

        if (ownsDestination && sessionInstance.isSet) {
            val destination = this.destination
            try {
                if (destination is TemporaryQueue) {
                    destination.delete()
                } else if (destination is TemporaryTopic) {
                    destination.delete()
                }
                this.commit()
            } catch(e: Exception) {
                log.error(e.message, e)
            }
        }

        // Close session if approrpriate
        if (ownsSession) {
            sessionInstance.ifSet(Action { s ->
                try {
                    s.close()
                } catch (e: Exception) {
                    log.error(e.message, e)
                }
            })
        }
        sessionInstance.reset()

        // Close connection
        if (ownsConnection) {
            connection.ifSet(Action { c ->
                try {
                    c.close()
                } catch (e: Exception) {
                    log.error(e.message, e)
                }
            })
        }
        connection.reset()
    }

    override fun toString(): String {
        return this.configuration.toString()
    }

    protected fun finalize() {
        this.close()
    }
}
