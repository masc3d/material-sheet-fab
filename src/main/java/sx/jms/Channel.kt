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
 * Created by masc on 06.07.15.
 * @param connectionFactory JMS connection factory
 * @param session JMS sesssion. Optional, if omitted a dedicated session will be created for this channel.
 * @param destination JMS destination
 * @param converter Message converter
 * @param sessionTransacted Dedicated session for this channel should be transacted. Defaults to true
 * A timeout of zero blocks indefinitely.
 */
class Channel private constructor(
        private val connectionFactory: ConnectionFactory? = null,
        session: Session? = null,
        private val destination: Destination,
        private val converter: Converter,
        private val sessionTransacted: Boolean = Defaults.JMS_TRANSACTED,
        private val deliveryMode: Channel.DeliveryMode = Defaults.JMS_DELIVERY_MODE)
:
        Disposable,
        Closeable,
        Cloneable {

    /**
     * c'tor for creating channel using a new connection created via connection factory
     */
    @JvmOverloads constructor(connectionFactory: ConnectionFactory,
                              sessionTransacted: Boolean = Channel.JMS_TRANSACTED,
                              deliveryMode: Channel.DeliveryMode = Channel.JMS_DELIVERY_MODE,
                              destination: Destination,
                              converter: Converter) : this(
            connectionFactory = connectionFactory,
            session = null,
            sessionTransacted = sessionTransacted,
            destination = destination,
            converter = converter,
            deliveryMode = deliveryMode) {
    }

    /**
     * c'tor for creating channel using an existing session
     */
    @JvmOverloads constructor(session: Session,
                              jmsDeliveryMode: Channel.DeliveryMode = Channel.JMS_DELIVERY_MODE,
                              destination: Destination,
                              converter: Converter) : this(
            connectionFactory = null,
            session = session,
            destination = destination,
            converter = converter,
            sessionTransacted = session.transacted,
            deliveryMode = jmsDeliveryMode) {
    }

    companion object Defaults {
        private val RECEIVE_TIMEOUT = Duration.ofSeconds(10)
        private val JMS_TTL = Duration.ZERO
        private val JMS_TRANSACTED = true
        private val JMS_DELIVERY_MODE = Channel.DeliveryMode.NonPersistent
    }

    private val log = LogFactory.getLog(this.javaClass)
    private val connection = LazyInstance<Connection>()
    private val session = LazyInstance<Session>()
    private val consumer = LazyInstance<MessageConsumer>()
    private var sessionCreated = false

    /** Time to live for messages sent through this channel */
    var ttl: Duration = Defaults.JMS_TTL
    /** Time to live for messages sent through this channel */
    var priority: Int? = null
    /** Auto-commit messages on send, defaults to true */
    var autoCommit: Boolean = true
    /** Default receive timeout for receive/sendReceive calls. Defaults to 10 seconds. */
    var receiveTimeout: Duration = Defaults.RECEIVE_TIMEOUT

    /**
     * Temporary response queue
     */
    private val temporaryResponseQueue = LazyInstance<TemporaryQueue>({
        this.session.get().createTemporaryQueue()
    })

    /**
     * Temporary response queue consumer
     */
    private var temporaryResponseQueueConsumer = LazyInstance<MessageConsumer>({
        this.session.get().createConsumer(this.temporaryResponseQueue.get())
    })

    /**
     * Delivery modes
     */
    enum class DeliveryMode(val value: Int) {
        NonPersistent(javax.jms.DeliveryMode.NON_PERSISTENT),
        Persistent(javax.jms.DeliveryMode.PERSISTENT)
    }

    init {
        // Initialize lazy properties

        // JMS connection
        this.connection.set(fun(): Connection {
            if (this.connectionFactory == null)
                throw IllegalStateException("Channel does not have connection factory")

            var cn = connectionFactory.createConnection()
            cn!!.start()
            return cn
        })

        // JMS session
        this.session.set(fun(): Session {
            // Return c'tor provided session if applicable
            if (session != null)
                return session

            // Create session
            sessionCreated = true
            return connection.get().createSession(this.sessionTransacted, Session.AUTO_ACKNOWLEDGE)
        })

        // JMS consumer
        this.consumer.set(fun(): MessageConsumer {
            return this.session.get().createConsumer(destination)
        })
    }

    /**
     * Deletes temporary response queue and its consumer
     */
    private fun deleteTemporaryResponseQueue() {
        this.temporaryResponseQueue.ifSet { q ->
            this.temporaryResponseQueueConsumer.ifSet { c ->
                c.close()
            }
            // Commit before deleting temporary response queue
            this.commit()

            q.delete()
        }
        this.temporaryResponseQueue.reset()
        this.temporaryResponseQueueConsumer.reset()
    }

    /**
     * Send jms message
     * @param message Message to send
     * @param replyDestination Optional reply destination. If given, send will return a temporary channel
     * @param messageConfigurer Callback for customizing the message before sending
     * @return Optional: temporary channel, depending on replyDestination
     */
    @JvmOverloads fun send(message: Message,
                           replyDestination: Destination? = null,
                           messageConfigurer: Action<Message>? = null): Channel? {
        val mp = session.get().createProducer(destination)

        mp.deliveryMode = deliveryMode.value
        mp.timeToLive = ttl.toMillis()
        val priority = priority
        if (priority != null)
            mp.priority = priority

        messageConfigurer?.perform(message)

        mp.send(destination, message)
        mp.close()

        if (this.autoCommit)
            this.commit()

        return null
    }

    /**
     * Send object as message using converter
     * @param message
     * @param messageConfigurer Callback for customizing the message before sending
     */
    @JvmOverloads fun send(message: Any, messageConfigurer: Action<Message>? = null) {
        this.send(converter.toMessage(message, session.get()), messageConfigurer)
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
     * Request/response type send and receive using a temporary response queue
     * @param request Request message
     * @param responseType Response class type
     * @param requestMessageConfigurer Request message configurer
     * @param preserveTemporaryResponseQueue Preserve temporary response queue (more efficient when channel is reused)
     */
    @Throws(TimeoutException::class)
    fun <T> sendReceive(
            request: Any,
            responseType: Class<T>,
            requestMessageConfigurer: Action<Message>? = null,
            preserveTemporaryResponseQueue: Boolean = false): T {

        try {
            this.send(request, Action { m ->
                m.jmsReplyTo = this.temporaryResponseQueue.get()
                requestMessageConfigurer?.perform(m)
            })

            return this.receive(
                    consumer = this.temporaryResponseQueueConsumer.get(),
                    messageType = responseType)
        } finally {
            // Delete temporary response queue if it should not be preserved
            if (!preserveTemporaryResponseQueue) {
                this.deleteTemporaryResponseQueue()
            }
        }
    }

    /**
     * Explicitly commit transaction if the session is transacted.
     * If the session is not transacted invoking this method has no effect.
     */
    fun commit() {
        val session = session.get()
        if (session.transacted)
            session.commit()
    }

    /**
     * Close channel
     */
    override fun close() {
        // Close temporary response queue
        temporaryResponseQueue.ifSet({ q ->
            try {
                q.delete()
            } catch(e: JMSException) {
                log.error(e.message, e)
            }
        })

        // Close message consumer
        consumer.ifSet({ c ->
            try {
                c.close()
            } catch (e: JMSException) {
                log.error(e.message, e)
            }
        })

        // Commit session
        try {
            this.commit()
        } catch (e: JMSException) {
            log.error(e.message, e)
        }

        // Close session if approrpriate
        if (sessionCreated) {
            session.ifSet({ s ->
                try {
                    s.close()
                } catch (e: JMSException) {
                    log.error(e.message, e)
                }
            })
        }

        // Close connection
        connection.ifSet({ c ->
            try {
                c.close()
            } catch (e: JMSException) {
                log.error(e.message, e)
            }
        })
    }
}
