package sx.jms

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import sx.Action
import sx.Disposable
import sx.Dispose
import sx.LazyInstance

import java.io.Closeable
import java.time.Duration
import java.util.function.Supplier
import javax.jms.*

/**
 * Lightweight messaging channel with send/receive and automatic message conversion capabilities.
 * Caches session and message consumer/producer.
 * Created by masc on 06.07.15.
 */
class Channel
/**
 * c'tor
 * @param connectionFactory Connection factory used to create session
 * @param destination Destination for this channel
 * @param converter Message converter to use
 * @param receiveTimeout Timeout when receiving messages. Defaults to 10 seconds.
 * @param transacted Session transacted or not
 * @param deliveryMode JMS delivery mode
 * @param ttl JMS message time to live
 * @param priority JMS message priority
 */
@JvmOverloads constructor(
        private val connectionFactory: ConnectionFactory,
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

    enum class DeliveryMode(val value: Int) {
        NonPersistent(javax.jms.DeliveryMode.NON_PERSISTENT),
        Persistent(javax.jms.DeliveryMode.PERSISTENT)
    }

    init {
        connection.set(fun(): Connection {
            var cn = connectionFactory.createConnection()
            cn!!.start()
            return cn
        })

        session.set(fun(): Session {
            val session = connection.get().createSession(this.jmsSessionTransacted, this.jmsDeliveryMode.value)
            sessionCreated = true
            return session
        })

        consumer.set(fun(): MessageConsumer {
            return session.get().createConsumer(destination)
        })
    }

    /**
     * Send jms message
     * @param message Message to send
     * *
     * @param messageConfigurer Callback for customizing the message before sending
     */
    fun send(message: Message, messageConfigurer: Action<Message>?) {
        val mp = session.get().createProducer(destination)

        mp.deliveryMode = jmsDeliveryMode.value
        mp.timeToLive = jmsTtl.toMillis()
        if (jmsPriority != null)
            mp.priority = jmsPriority

        messageConfigurer?.perform(message)

        mp.send(destination, message)
    }

    /**
     * Send jms message
     * @param message Message to send
     */
    fun send(message: Message) {
        this.send(message, null)
    }

    /**
     * Send object as message using converter
     * @param message
     */
    fun send(message: Any) {
        this.send(converter.toMessage(message, session.get()))
    }

    /**
     * Receive message as object using converter
     * @param messageType Type of message
     */
    fun <T> receive(messageType: Class<T>): T {
        return messageType.cast(
                this.converter.fromMessage(
                        this.consumer.get()
                                .receive(this.receiveTimeout.toMillis())))
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
                log.error(e.getMessage(), e)
            }
        })

        try {
            this.commit()
        } catch (e: JMSException) {
            log.error(e.getMessage(), e)
        }


        if (sessionCreated) {
            session.ifSet({ s ->
                try {
                    s.close()
                } catch (e: JMSException) {
                    log.error(e.getMessage(), e)
                }
            })
        }

        connection.ifSet({ c ->
            try {
                c.close()
            } catch (e: JMSException) {
                log.error(e.getMessage(), e)
            }
        })
    }

    override fun dispose() {
        this.close()
    }
}
