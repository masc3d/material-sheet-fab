package sx.jms

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import sx.Action
import sx.Disposable
import sx.Dispose
import sx.LazyInstance

import javax.jms.*
import javax.jms.IllegalStateException
import java.io.Closeable
import java.util.function.Supplier

/**
 * Lightweight messaging channel
 * Created by masc on 06.07.15.
 */
public class Channel
/**
 * c'tor
 * @param connectionFactory Connection factory used to create session
 * *
 * @param destination Destination for this channel
 * *
 * @param converter Message converter to use
 * *
 * @param transacted Session transacted or not
 * *
 * @param deliveryMode JMS delivery mode
 * *
 * @param ttl JMS message time to live
 * *
 * @param priority JMS message priority
 */
@jvmOverloads constructor(
        private val connectionFactory: ConnectionFactory,
        private val destination: Destination,
        private val converter: Converter?,
        private val jmsSessionTransacted: Boolean,
        private val jmsDeliveryMode: Int,
        private val jmsTtl: Long,
        private val jmsPriority: Int? = null) : Disposable, Closeable {

    private val log = LogFactory.getLog(this.javaClass)
    private val connection = LazyInstance<Connection>()
    private val session = LazyInstance<Session>()
    private val consumer = LazyInstance<MessageConsumer>()
    private var sessionCreated = false

    init {
        connection.set( fun (): Connection {
            var cn = connectionFactory.createConnection()
            cn!!.start()
            return cn
        } )

        session.set( fun (): Session {
            val session = connection.get().createSession(this.jmsSessionTransacted, this.jmsDeliveryMode)
            sessionCreated = true
            return session
        })

        consumer.set( fun(): MessageConsumer {
            return session.get().createConsumer(destination)
        })
    }

    /**
     * Send jms message
     * @param message Message to send
     * *
     * @param messageConfigurer Callback for customizing the message before sending
     */
    throws(JMSException::class)
    public fun send(message: Message, messageConfigurer: Action<Message>?) {
        val mp = session.get().createProducer(destination)

        mp.setDeliveryMode(jmsDeliveryMode)
        mp.setTimeToLive(jmsTtl)
        if (jmsPriority != null)
            mp.setPriority(jmsPriority)

        messageConfigurer?.perform(message)

        mp.send(destination, message)
    }

    /**
     * Send jms message
     * @param message Message to send
     */
    throws(JMSException::class)
    public fun send(message: Message) {
        this.send(message, null)
    }

    /**
     * Send object as message using converter
     * @param message
     */
    throws(JMSException::class)
    public fun send(message: Any) {
        if (converter == null)
            throw IllegalStateException("Cannot send object without a message converter")

        this.send(converter.toMessage(message, session.get()))
    }

    throws(JMSException::class)
    public fun <T> receive(messageType: Class<T>): T {
        return null
    }

    /**
     * Explicitly commit transaction
     */
    throws(JMSException::class)
    public fun commit() {
        val session = session.get()
        if (session.getTransacted())
            session.commit()
    }

    override fun close() {
        consumer.ifSet( { c ->
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
            session.ifSet( { s ->
                try {
                    s.close()
                } catch (e: JMSException) {
                    log.error(e.getMessage(), e)
                }
            })
        }

        connection.ifSet( { c ->
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
