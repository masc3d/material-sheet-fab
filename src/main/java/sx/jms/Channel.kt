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
 */
class Channel private constructor(
        private val connectionFactory: ConnectionFactory? = null,
        session: Session? = null,
        private val destination: Destination,
        private val converter: Converter,
        private val receiveTimeout: Duration = Duration.ofSeconds(10),
        private val jmsSessionTransacted: Boolean,
        private val jmsDeliveryMode: Channel.DeliveryMode,
        private val jmsTtl: Duration,
        private val jmsPriority: Int? = null) : Disposable, Closeable {

    private val log = LogFactory.getLog(this.javaClass)
    private val connection = LazyInstance<Connection>()
    private val session = LazyInstance<Session>()
    private val consumer = LazyInstance<MessageConsumer>()
    private var sessionCreated = false

    private val temporaryResponseQueue: Destination by lazy ({
        this.session.get().createTemporaryQueue()
    })
    private val temporaryResponseQueueConsumer: MessageConsumer by lazy ({
        this.session.get().createConsumer(this.temporaryResponseQueue)
    })

    /**
     * c'tor for creating channel using a new connection created via connection factory
     */
    @JvmOverloads constructor(connectionFactory: ConnectionFactory,
                              destination: Destination,
                              converter: Converter,
                              receiveTimeout: Duration = Duration.ofSeconds(10),
                              jmsSessionTransacted: Boolean = true,
                              jmsDeliveryMode: Channel.DeliveryMode,
                              jmsTtl: Duration,
                              jmsPriority: Int? = null) : this(
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
                receiveTimeout: Duration = Duration.ofSeconds(10),
                jmsDeliveryMode: Channel.DeliveryMode,
                jmsTtl: Duration,
                jmsPriority: Int? = null) : this(
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

    private fun <T> receive(consumer: MessageConsumer, messageType: Class<T>): T {
        val jmsMessage = consumer.receive(this.receiveTimeout.toMillis())

        if (jmsMessage == null)
            throw TimeoutException("Timeout while waiting for message [${messageType.simpleName}]")

        return messageType.cast(this.converter.fromMessage(jmsMessage))
    }

    /**
     * Request/response type send and receive
     * @param request Request message
     * @param responseType Response class type
     * @param requestMessageConfigurer Request message configurer
     * @param useTemporaryResponseQueue Use a temporary response queue
     */
    @Throws(TimeoutException::class)
    fun <T> sendReceive(
            request: Any,
            responseType: Class<T>,
            requestMessageConfigurer: Action<Message>? = null,
            useTemporaryResponseQueue: Boolean = false): T {
        this.send(request, Action { m ->
            m.jmsReplyTo = this.temporaryResponseQueue
            requestMessageConfigurer?.perform(m)
        })

        return this.receive(
                consumer = if (useTemporaryResponseQueue) this.temporaryResponseQueueConsumer else this.consumer.get(),
                messageType = responseType)
    }

    /**
     * Explicitly commit transaction
     */
    fun commit() {
        val session = session.get()
        if (session.transacted)
            session.commit()
    }

    override fun close() {
        consumer.ifSet({ c ->
            try {
                c.close()
            } catch (e: JMSException) {
                log.error(e.message, e)
            }
        })

        try {
            this.commit()
        } catch (e: JMSException) {
            log.error(e.message, e)
        }


        if (sessionCreated) {
            session.ifSet({ s ->
                try {
                    s.close()
                } catch (e: JMSException) {
                    log.error(e.message, e)
                }
            })
        }

        connection.ifSet({ c ->
            try {
                c.close()
            } catch (e: JMSException) {
                log.error(e.message, e)
            }
        })
    }

    override fun dispose() {
        this.close()
    }
}
