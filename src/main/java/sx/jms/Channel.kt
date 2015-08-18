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
        private val mConnectionFactory: ConnectionFactory,
        private val mDestination: Destination,
        private val mConverter: Converter?,
        private val mJmsSessionTransacted: Boolean,
        private val mJmsDeliveryMode: Int,
        private val mJmsTtl: Long,
        private val mJmsPriority: Int? = null) : Disposable, Closeable {

    private val mLog = LogFactory.getLog(this.javaClass)
    private val mConnection = LazyInstance<Connection>()
    private val mSession = LazyInstance<Session>()
    private val mConsumer = LazyInstance<MessageConsumer>()
    private var mSessionCreated = false

    init {
        mConnection.set( fun (): Connection {
            var cn = mConnectionFactory.createConnection()
            cn!!.start()
            return cn
        } )

        mSession.set( fun (): Session {
            val session = mConnection.get().createSession(this.mJmsSessionTransacted, this.mJmsDeliveryMode)
            mSessionCreated = true
            return session
        })

        mConsumer.set( fun(): MessageConsumer {
            return mSession.get().createConsumer(mDestination)
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
        val mp = mSession.get().createProducer(mDestination)

        mp.setDeliveryMode(mJmsDeliveryMode)
        mp.setTimeToLive(mJmsTtl)
        if (mJmsPriority != null)
            mp.setPriority(mJmsPriority)

        messageConfigurer?.perform(message)

        mp.send(mDestination, message)
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
        if (mConverter == null)
            throw IllegalStateException("Cannot send object without a message converter")

        this.send(mConverter.toMessage(message, mSession.get()))
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
        val session = mSession.get()
        if (session.getTransacted())
            session.commit()
    }

    override fun close() {
        mConsumer.ifSet( { c ->
            try {
                c.close()
            } catch (e: JMSException) {
                mLog.error(e.getMessage(), e)
            }
        })

        try {
            this.commit()
        } catch (e: JMSException) {
            mLog.error(e.getMessage(), e)
        }


        if (mSessionCreated) {
            mSession.ifSet( { s ->
                try {
                    s.close()
                } catch (e: JMSException) {
                    mLog.error(e.getMessage(), e)
                }
            })
        }

        mConnection.ifSet( { c ->
            try {
                c.close()
            } catch (e: JMSException) {
                mLog.error(e.getMessage(), e)
            }
        })
    }

    override fun dispose() {
        this.close()
    }
}
