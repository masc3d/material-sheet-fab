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
 * @param jmsSessionTransacted Dedicated session for this channel should be transacted. Defaults to true
 * @param autoCommit Auto-commit messages on send, defaults to true
 * @param jmsTtl Time to live for messages sent through this channel
 * @param jmsPriority Priority for messages sent through this channel
 * @param receiveTimeout Default receive timeout for receive/sendReceive calls. Defaults to 10 seconds.
 * A timeout of zero blocks indefinitely.
 */
class Channel private constructor(
        private val connectionFactory: ConnectionFactory? = null,
        session: Session? = null,
        private val destination: Destination,
        private val converter: Converter,
        private val jmsSessionTransacted: Boolean = Channel.JMS_TRANSACTED,
        private val jmsDeliveryMode: Channel.DeliveryMode = Channel.JMS_DELIVERY_MODE,
        private val jmsTtl: Duration = Channel.JMS_TTL,
        private val jmsPriority: Int? = null,
        private val autoCommit: Boolean = true,
        private val receiveTimeout: Duration = Channel.RECEIVE_TIMEOUT)
:
        Disposable,
        Closeable {

    /**
     * c'tor for creating channel using a new connection created via connection factory
     */
    @JvmOverloads constructor(connectionFactory: ConnectionFactory,
                              destination: Destination,
                              converter: Converter,
                              jmsSessionTransacted: Boolean = Channel.JMS_TRANSACTED,
                              jmsDeliveryMode: Channel.DeliveryMode = Channel.JMS_DELIVERY_MODE,
                              jmsTtl: Duration = Channel.JMS_TTL,
                              jmsPriority: Int? = null,
                              receiveTimeout: Duration = Channel.RECEIVE_TIMEOUT) : this(
            connectionFactory = connectionFactory,
            session = null,
            destination = destination,
            converter = converter,
            receiveTimeout = receiveTimeout,
            jmsSessionTransacted = jmsSessionTransacted,
            jmsDeliveryMode = jmsDeliveryMode,
            jmsTtl = jmsTtl,
            jmsPriority = jmsPriority) {
    }

    /**
     * c'tor for creating channel using an existing session
     */
    @JvmOverloads constructor(session: Session,
                              destination: Destination,
                              converter: Converter,
                              jmsDeliveryMode: Channel.DeliveryMode = Channel.JMS_DELIVERY_MODE,
                              jmsTtl: Duration = Channel.JMS_TTL,
                              jmsPriority: Int? = null,
                              receiveTimeout: Duration = Channel.RECEIVE_TIMEOUT) : this(
            connectionFactory = null,
            session = session,
            destination = destination,
            converter = converter,
            receiveTimeout = receiveTimeout,
            jmsSessionTransacted = session.transacted,
            jmsDeliveryMode = jmsDeliveryMode,
            jmsTtl = jmsTtl,
            jmsPriority = jmsPriority) {
    }

    companion object {
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
            return connection.get().createSession(this.jmsSessionTransacted, Session.AUTO_ACKNOWLEDGE)
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
     * @param messageConfigurer Callback for customizing the message before sending
     */
    @JvmOverloads fun send(message: Message, messageConfigurer: Action<Message>? = null) {
        val mp = session.get().createProducer(destination)

        mp.deliveryMode = jmsDeliveryMode.value
        mp.timeToLive = jmsTtl.toMillis()
        if (jmsPriority != null)
            mp.priority = jmsPriority

        messageConfigurer?.perform(message)

        mp.send(destination, message)
        mp.close()

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
     * Receive message as object using converter
     * @param messageType Type of message
     */
    @Throws(TimeoutException::class)
    fun <T> receive(messageType: Class<T>): T {
        return this.receive(this.consumer.get(), messageType)
    }

    /**
     * Receive message
     */
    private fun <T> receive(consumer: MessageConsumer, messageType: Class<T>): T {
        val jmsMessage = if (this.receiveTimeout == Duration.ZERO)
            consumer.receive()
        else
            consumer.receive(this.receiveTimeout.toMillis())

        if (jmsMessage == null)
            throw TimeoutException("Timeout while waiting for message [${messageType.simpleName}]")

        return messageType.cast(this.converter.fromMessage(jmsMessage))
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
