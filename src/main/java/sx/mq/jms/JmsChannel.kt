package sx.mq.jms

import org.slf4j.LoggerFactory
import org.threeten.bp.Duration
import sx.LazyInstance
import sx.io.serialization.Serializer
import java.util.*
import java.util.concurrent.TimeoutException
import javax.jms.Connection
import javax.jms.ConnectionFactory
import javax.jms.Destination
import javax.jms.IllegalStateException
import javax.jms.Message
import javax.jms.MessageConsumer
import javax.jms.MessageProducer
import javax.jms.Session
import javax.jms.TemporaryQueue
import javax.jms.TemporaryTopic

/**
 * Lightweight messaging channel with send/receive and automatic message conversion capabilities.
 * Caches session and message consumer/producer.
 * TODO: consider seperating channel configuration from actual implementation, as eg. listeners have their dedicated session transaction setting, so it may be slightly confusing
 * TODO: add support for actively supporting correlation id for reusing temporary queues/channels in a request/response scheme
 * TODO: add support for temporary queue pooling, to prevent creation of temporary queues per request (sendRequest) @link TemporaryConnectionPool
 * Created by masc on 06.07.15.
 * @param endpoint Channel
 * @param session Optional: jms session to use
 */
class JmsChannel @JvmOverloads constructor(
        val endpoint: JmsEndpoint,
        session: Session? = null)
    :
        sx.mq.MqChannel {

    companion object {
        val DEFAULT_RECEIVE_TIMEOUT = Duration.ofSeconds(10)
        val DEFAULT_JMS_TTL = Duration.ZERO
        val DEFAULT_JMS_TRANSACTED = true
        val DEFAULT_JMS_DELIVERY_MODE = JmsChannel.DeliveryMode.NonPersistent
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
                val list = queue.get(t.key)
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

    /**
     * Lazy connection instance
     */
    private val connection = LazyInstance<Connection>(
            LazyInstance.ThreadSafetyMode.None)

    /**
     * Lazy session instance
     */
    private val sessionInstance = LazyInstance<Session>(
            LazyInstance.ThreadSafetyMode.None)

    /**
     * Lazy consumer instance
     */
    private val consumer = LazyInstance<MessageConsumer>(
            LazyInstance.ThreadSafetyMode.None)

    /**
     * Lazy producer instance
     */
    private val producer = LazyInstance<MessageProducer>(
            LazyInstance.ThreadSafetyMode.None)

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
        get() = this.endpoint.context?.connectionFactory

    val sessionTransacted: Boolean
        get() = this.endpoint.sessionTransacted

    val session: Session
        get() = this.sessionInstance.get()

    val destination: Destination
        get() = this.endpoint.destination

    val converter: JmsConverter
        get() = this.endpoint.converter

    val deliveryMode: DeliveryMode
        get() = this.endpoint.deliveryMode

    var ttl: Duration = this.endpoint.ttl

    var priority: Int? = this.endpoint.priority

    var autoCommit: Boolean = this.endpoint.autoCommit

    var receiveTimeout: Duration = this.endpoint.receiveTimeout

    /**
     * Channel statistics
     */
    val statistics: Statistics = Statistics()

    /**
     * init
     */
    init {
        // JMS connection
        this.connection.set({
            val cnf = this.connectionFactory
            if (cnf == null)
                throw IllegalStateException("Channel does not have connection factory")

            val cn = cnf.createConnection()
            cn.start()

            this.ownsConnection = true

            cn
        })

        // JMS session
        this.sessionInstance.set({
            val finalSession: Session
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
        this.consumer.set({
            this.sessionInstance.get().createConsumer(destination)
        })

        // JMS producer
        this.producer.set({
            this.sessionInstance.get().createProducer(destination)
        })
    }

    /**
     * Create a client for side-band responding on a temporary queue using this channels basic settings
     * @param session: Optional session: If not provided this channel's session will be used
     * @param destination: Optinoal destination. If not provided an internal one will be created and destroyed on channel close
     * @return Temporary queue channel
     */
    private fun createReplyClientInternal(session: Session = this.sessionInstance.get(), destination: Destination? = null): JmsChannel {
        val replyChannel: JmsChannel
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

        replyChannel = JmsChannel(
                endpoint = this.endpoint.clone(
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
    fun createReplyClient(session: Session, destination: Destination): JmsChannel {
        return this.createReplyClientInternal(session, destination)
    }

    /**
     * Create reply channel
     */
    fun createReplyClient(): JmsChannel {
        return this.createReplyClientInternal(destination = null)
    }

    /**
     * Send jms message
     * @param jmsMessage Message to send
     * @param messageConfigurer Callback for customizing the message before sending
     */
    @JvmOverloads fun send(jmsMessage: Message, messageConfigurer: ((Message) -> Unit)? = null) {
        val mp = this.producer.get()

        mp.deliveryMode = deliveryMode.value
        mp.timeToLive = ttl.toMillis()
        val priority = priority
        if (priority != null)
            mp.priority = priority

        // Customize message if applicable
        messageConfigurer?.invoke(jmsMessage)

        // Send actual message
        mp.send(jmsMessage)

        if (this.autoCommit)
            this.commit()
    }

    /**
     * Sends jms message as a request, attaching a replyTo queue
     * @param jmsMessage Message to send
     * @param messageConfigurer Callback for customizing the message before sending
     */
    @JvmOverloads fun sendRequest(jmsMessage: Message,
                                  replyChannel: JmsChannel = this.createReplyClientInternal(),
                                  messageConfigurer: ((Message) -> Unit)? = null): JmsChannel {
        this.send(
                jmsMessage = jmsMessage,
                messageConfigurer = {
                    messageConfigurer?.invoke(it)
                    it.jmsReplyTo = replyChannel.destination
                })

        return replyChannel
    }

    /**
     * Send object as message using converter
     * @param message
     * @param messageConfigurer Callback for customizing the message before sending
     */
    fun send(message: Any, messageConfigurer: ((Message) -> Unit)?) {
        this.send(
                jmsMessage = this.converter.toMessage(
                        obj = message,
                        session = sessionInstance.get(),
                        onSize = statistics.accountSent),
                messageConfigurer = messageConfigurer)
    }

    /**
     * Send object as message using converter
     * @param message
     */
    override fun send(message: Any) {
        this.send(
                message = message,
                messageConfigurer = null)
    }

    /**
     * Sends message as a request, attaching a replyTo queue
     * Creates a temporary queue and referring channel and returns it for receiving the response
     * @param message Message to send
     * @param messageConfigurer Optional callback to customize jms message
     * @return Response/reply channel
     */
    @JvmOverloads fun sendRequest(message: Any,
                                  replyChannel: JmsChannel = this.createReplyClientInternal(),
                                  messageConfigurer: ((Message) -> Unit)? = null): JmsChannel {
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
    override fun <T> receive(messageType: Class<T>): T {
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

        Serializer.types.register(messageType)

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
        consumer.ifSet({ c ->
            try {
                c.close()
            } catch (e: Exception) {
                log.error(e.message, e)
            }
        })
        consumer.reset()

        // Close message producer
        producer.ifSet({ p ->
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
            sessionInstance.ifSet({ s ->
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
            connection.ifSet({ c ->
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
        return this.endpoint.toString()
    }

    protected fun finalize() {
        this.close()
    }
}