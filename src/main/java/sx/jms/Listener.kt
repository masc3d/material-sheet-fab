package sx.jms

import com.google.common.reflect.TypeToken
import org.slf4j.LoggerFactory
import sx.Disposable
import sx.logging.slf4j.*
import java.io.Serializable
import java.lang.reflect.ParameterizedType
import java.util.*
import java.util.concurrent.Executor
import javax.jms.ExceptionListener
import javax.jms.JMSException
import javax.jms.Message
import javax.jms.Session

/**
 * Lightweight jms message listener abstraction.
 * This is the top level abstract class, only binding a connection factory.
 * Created by masc on 16.04.15.
 * @property channel Messaging channel
 * @property executor Thread executor
 */
abstract class Listener(
        /** Connection factory  */
        channel: () -> Channel,
        protected val executor: Executor)
:
        Disposable,
        ExceptionListener {
    protected val log = LoggerFactory.getLogger(this.javaClass)
    /** Object message handler delegates  */
    private val handlerDelegates = HashMap<Class<*>, Handler<Any?>>()

    /**
     * Message handling exception
     */
    private inner class HandlingException(message: String) : RuntimeException(message)

    @Throws(JMSException::class)
    abstract fun start()

    @Throws(JMSException::class)
    abstract fun stop()

    private val lazyChannel: () -> Channel

    init {
        this.lazyChannel = channel
    }

    /**
     * Messaging channel
     */
    val channel: Channel by lazy {
        this.lazyChannel()
    }

    /**
     * Default message handler with support for object message delegates
     * @param message
     * @param session
     * @throws JMSException
     */
    @Throws(JMSException::class)
    protected open fun onMessage(message: Message, session: Session) {
        var messageObject: Any? = null

        val handler: Handler<Any?>?

        // Deserialize if there's a converter and determine handler
        val converter = this.channel.converter

        try {
            messageObject = converter.fromMessage(message)
        } catch(e: Exception) {
            log.error("Error converting message [${message}] ${e.message}")
        }

        if (messageObject != null) {
            handler = this.handlerDelegates.getOrDefault(messageObject.javaClass, null)

            if (handler == null) {
                throw HandlingException("No delegate for message object type [%s]".format(messageObject.javaClass, Message::class.java))
            }

            // Prepare reply channel if applicable
            var replyChannel: Channel? = null
            if (message.jmsReplyTo != null) {
                replyChannel = this.channel.createReplyChannel(session, message.jmsReplyTo)
            }

            // Delegate to handler
            try {
                handler.onMessage(messageObject, replyChannel)
            } finally {
                if (replyChannel != null)
                    replyChannel.close()
            }
        }
    }

    /**
     * Default error handler, simply logging the error
     * @param e Error
     */
    protected open fun onError(e: Throwable) {
        log.error(e.message, e)
    }

    /**
     * Add handler delegate for handling messages of specific (object) type
     * Delegate handlers requires a converter to be set.
     * @param delegate Handler
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> addDelegate(delegate: Handler<T>) {
        var messageTypes = delegate.messageTypes
        if (messageTypes.size == 0) {
            // No message types specified, extracing generic type from Handler interface
            val handlerInterfaces = TypeToken.of(delegate.javaClass).types.interfaces()

            // Find interface type for Handler<T> in hierarchy
            val handlerInterface = handlerInterfaces
                    .filter { it.isSubtypeOf(Handler::class.java) }
                    .first()

            val handlerInterfaceType = handlerInterface.type
            if (!(handlerInterfaceType is ParameterizedType))
                throw IllegalArgumentException()

            messageTypes = arrayListOf(handlerInterfaceType.actualTypeArguments.get(0) as Class<*>)
        }

        for (c in messageTypes) {
            // Verify if message/type is serializable
            if (TypeToken.of(c).types.interfaces().filter { it.rawType.equals(Serializable::class.java) }.size == 0)
                throw IllegalArgumentException("Message type [${c.name}] is not serializable")

            if (this.handlerDelegates.containsKey(c))
                throw IllegalStateException("Listener already contains handler for message type [${c.name}]")

            log.debug("Registering message handler [${delegate.javaClass.name}] for [${c.name}]")
            handlerDelegates.put(c, delegate as Handler<Any?>)
        }
    }

    final override fun onException(e: JMSException) {
        this.onError(e)
    }

    override fun close() {
        try {
            this.stop()
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }
}
