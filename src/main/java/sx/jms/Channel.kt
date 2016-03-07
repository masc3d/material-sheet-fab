package sx.jms

import org.apache.commons.logging.LogFactory
import sx.Action
import sx.Disposable
import sx.LazyInstance
import java.io.Closeable
import java.time.Duration
import java.util.concurrent.TimeoutException
import javax.jms.*

/**
 * Lightweight messaging channel with send/receive and automatic message conversion capabilities.
 * Caches session and message consumer/producer.
 * TODO: consider seperating channel configuration from actual implementation, as eg. listeners have their dedicated session transaction setting, so it may be slightly confusing
 * TODO: add support for actively supporting correlation id for reusing temporary queues/channels in a request/response scheme
 * TODO: add support for temporary queue pooling, to prevent creation of temporary queues per request (sendRequest)
 * Created by masc on 06.07.15.
 * @param configuration Channel configuration
 * @param session Optional: jms session to use
 */
class Channel private constructor(
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

    companion object Defaults {
        private val RECEIVE_TIMEOUT = Duration.ofSeconds(10)
        private val JMS_TTL = Duration.ZERO
        private val JMS_TRANSACTED = true
        private val JMS_DELIVERY_MODE = Channel.DeliveryMode.NonPersistent
    }

    /**
     * Channel configuration
     * All common channel configuration settings are grouped into this shallow structure which
     * can be easily (and automatically) replicated
     */
    class Configuration(val connectionFactory: ConnectionFactory?,
                        val sessionTransacted: Boolean,
                        destination: Destination,
                        val converter: Converter,
                        deliveryMode: Channel.DeliveryMode)
    :
            Cloneable {
        var destination: Destination
            private set

        var deliveryMode: DeliveryMode
            private set

        init {
            this.destination = destination
            this.deliveryMode = deliveryMode
        }

        /** Time to live for messages sent through this channel */
        var ttl: Duration = Defaults.JMS_TTL
        /** Priority for messages sent through this channel */
        var priority: Int? = null
        /** Auto-commit messages on send, defaults to true */
        var autoCommit: Boolean = true
        /** Default receive timeout for receive/sendReceive calls. Defaults to 10 seconds. */
        var receiveTimeout: Duration = Defaults.RECEIVE_TIMEOUT

        /**
         * Clone channel configuration, optionally overriding properties
         * @param destination Override destination
         * @param deliveryMode Override delivery mode
         */
        fun clone(
                destination: Destination? = null,
                deliveryMode: Channel.DeliveryMode? = null): Configuration {
            val newChannel = this.clone() as Configuration
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

    private val log = LogFactory.getLog(this.javaClass)
    private val configuration: Configuration
    private val connection = LazyInstance<Connection>()
    private val sessionInstance = LazyInstance<Session>()
    private val consumer = LazyInstance<MessageConsumer>()
    private val producer = LazyInstance<MessageProducer>()
    private var sessionCreated = false
    /** Indicates if this channel owns this destination, eg. when creating a response channel without explcitly providing
     * a destination. In this case the destination will be deleted when the channel is closed.
     */
    private var ownsDestination = false

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
     * Delivery modes
     */
    enum class DeliveryMode(val value: Int) {
        NonPersistent(javax.jms.DeliveryMode.NON_PERSISTENT),
        Persistent(javax.jms.DeliveryMode.PERSISTENT)
    }

    init {
        this.configuration = configuration
        // Initialize lazy properties

        // JMS connection
        this.connection.set({
            val cnf = this.connectionFactory
            if (cnf == null)
                throw IllegalStateException("Channel does not have connection factory")

            val cn = cnf.createConnection()
            cn.start()
            cn
        })

        // JMS session
        this.sessionInstance.set({
            var finalSession: Session
            // Return c'tor provided session if applicable
            if (session != null) {
                finalSession = session
            } else {
                // Create session
                sessionCreated = true
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
     * Create temporary queue channel replicating this channel's basic properties

     * @return Temporary queue channel
     */
    private fun createReplyChannel(): Channel {
        // Temporary queue channels should not be transacted.
        // EG. ActiveMQ has a problem with temporary destinations and transacted sessions, resulting in wrong message count
        // and (non-fatal) messages about pending queue entries when deleting transacted temporary queues

        val replyChannel: Channel
        var replySession: Session? = null
        if (!this.sessionTransacted) {
            // Session is not transacted, simply reuse it
            replySession = this.sessionInstance.get()
        } else {
            if (this.connectionFactory == null)
                throw java.lang.IllegalStateException("Cannot create response channel from transacted channel without connection factory")
        }

        replyChannel = Channel(
                configuration = this.configuration.clone(
                        destination = this.sessionInstance.get().createTemporaryQueue(),
                        deliveryMode = DeliveryMode.NonPersistent),
                session = replySession)

        // As this channel has ownership over destination, should close and delete appropriately
        replyChannel.ownsDestination = true

        return replyChannel
    }

    /**
     * Send jms message
     * @param message Message to send
     * @param messageConfigurer Callback for customizing the message before sending
     */
    @JvmOverloads fun send(message: Message, messageConfigurer: Action<Message>? = null) {
        val mp = this.producer.get()

        mp.deliveryMode = deliveryMode.value
        mp.timeToLive = ttl.toMillis()
        val priority = priority
        if (priority != null)
            mp.priority = priority

        messageConfigurer?.perform(message)

        mp.send(destination, message)

        if (this.autoCommit)
            this.commit()
    }

    /**
     * Sends jms message as a request, attaching a replyTo queue
     * @param message Message to send
     * @param messageConfigurer Callback for customizing the message before sending
     */
    @JvmOverloads fun sendRequest(message: Message, messageConfigurer: Action<Message>? = null): Channel {
        val replyChannel = this.createReplyChannel()

        this.send(message, Action {
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
                        message,
                        sessionInstance.get()),
                messageConfigurer)
    }

    /**
     * Sends message as a request, attaching a replyTo queue
     * Creates a temporary queue and referring channel and returns it for receiving the response
     * @param message Message to send
     * @param messageConfigurer Optional callback to customize jms message
     * @return Response/reply channel
     */
    @JvmOverloads fun sendRequest(message: Any, messageConfigurer: Action<Message>? = null): Channel {
        return this.sendRequest(
                this.converter.toMessage(
                        message,
                        sessionInstance.get()),
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

        return messageType.cast(this.converter.fromMessage(jmsMessage))
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

        // Close message producer
        producer.ifSet({ p ->
            try {
                p.close()
            } catch (e: Exception) {
                log.error(e.message, e)
            }
        })

        // Commit session (must occur after closing consumer(s))
        try {
            this.commit()
        } catch (e: Exception) {
            log.error(e.message, e)
        }

        if (ownsDestination) {
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
        if (sessionCreated) {
            sessionInstance.ifSet({ s ->
                try {
                    s.close()
                } catch (e: Exception) {
                    log.error(e.message, e)
                }
            })
        }

        // Close connection
        connection.ifSet({ c ->
            try {
                c.close()
            } catch (e: Exception) {
                log.error(e.message, e)
            }
        })
    }

    override fun toString(): String {
        return this.configuration.toString()
    }
}
