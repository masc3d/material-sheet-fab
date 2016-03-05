package sx.jms

import org.apache.commons.logging.LogFactory
import sx.Action
import sx.Disposable
import sx.LazyInstance
import java.io.Closeable
import java.time.Duration
import java.util.concurrent.TimeoutException
import java.util.function.Supplier
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
    private val producer = LazyInstance<MessageProducer>()
    private var sessionCreated = false
    private var deleteDestinationonClose = false

    /** Time to live for messages sent through this channel */
    var ttl: Duration = Defaults.JMS_TTL
    /** Time to live for messages sent through this channel */
    var priority: Int? = null
    /** Auto-commit messages on send, defaults to true */
    var autoCommit: Boolean = true
    /** Default receive timeout for receive/sendReceive calls. Defaults to 10 seconds. */
    var receiveTimeout: Duration = Defaults.RECEIVE_TIMEOUT

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
        this.connection.set(Supplier {
            if (this.connectionFactory == null)
                throw IllegalStateException("Channel does not have connection factory")

            val cn = connectionFactory.createConnection()
            cn.start()
            cn
        })

        // JMS session
        this.session.set(Supplier {
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
        this.consumer.set(Supplier {
            this.session.get().createConsumer(destination)
        })

        // JMS producer
        this.producer.set(Supplier {
            this.session.get().createProducer(destination)
        })
    }

    /**
     * Create temporary queue channel replicating this channel's properties
     * @return Temporary channel
     */
    private fun createTemporaryQueueChannel(): Channel {
        val c = Channel(session = this.session.get(),
                sessionTransacted = this.sessionTransacted,
                deliveryMode = this.deliveryMode,
                converter = this.converter,
                destination = this.session.get().createTemporaryQueue())

        c.priority = this.priority
        c.ttl = this.ttl
        c.receiveTimeout = this.receiveTimeout
        c.autoCommit = this.autoCommit
        c.deleteDestinationonClose = true

        return c
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
     * Send object as message using converter
     * @param message
     * @param messageConfigurer Callback for customizing the message before sending
     */
    @JvmOverloads fun send(message: Any, messageConfigurer: Action<Message>? = null) {
        this.send(converter.toMessage(message, session.get()), messageConfigurer)
    }

    /**
     */
    fun sendWithReplyChannel(message: Any, messageConfigurer: Action<Message>? = null): Channel {
        val replyChannel = this.createTemporaryQueueChannel()

        this.send(message, Action {
            it.jmsReplyTo = replyChannel.destination
        })

        return replyChannel
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
        val session = session.get()
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

        if (deleteDestinationonClose) {
            try {
                if (this.destination is TemporaryQueue) {
                    this.destination.delete()
                } else if (this.destination is TemporaryTopic) {
                    this.destination.delete()
                }
                this.commit()
            } catch(e: Exception) {
                log.error(e.message, e)
            }
        }

        // Close session if approrpriate
        if (sessionCreated) {
            session.ifSet({ s ->
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
}
