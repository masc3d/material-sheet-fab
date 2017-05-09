package sx.mq.jms

import com.google.common.reflect.TypeToken
import org.slf4j.LoggerFactory
import sx.Disposable
import sx.io.serialization.Serializer
import java.lang.reflect.ParameterizedType
import java.util.*
import javax.jms.ExceptionListener
import javax.jms.JMSException
import javax.jms.Message
import javax.jms.Session

/**
 * Lightweight jms message listener abstraction with object conversion and dispatch support
 * One or more object message handlers can be registered with the listener, converted messages will be dispatched by type (class)
 * Created by masc on 16.04.15.
 * @property channel Messaging channel
 * @property executor Thread executor
 */
abstract class JmsListener(
        /** Connection factory  */
        val channel: JmsChannel)
:
        Disposable,
        ExceptionListener {
    protected val log = LoggerFactory.getLogger(this.javaClass)
    /** Object message handler delegates  */
    private val handlerDelegates = HashMap<Class<*>, JmsHandler<Any?>>()

    /**
     * Message handling exception
     */
    private inner class HandlingException(message: String) : RuntimeException(message)

    @Throws(JMSException::class)
    abstract fun start()

    @Throws(JMSException::class)
    abstract fun stop()

    protected val context by lazy {
        this.channel.context ?: throw IllegalStateException("Listener channel requires context")
    }

    /**
     * Messaging channel
     */
    private val client by lazy {
        JmsClient(channel)
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

        val jmsHandler: JmsHandler<Any?>?

        // Deserialize if there's a converter and determine handler
        val converter = this.channel.converter

        try {
            messageObject = converter.fromMessage(message)
        } catch(e: Exception) {
            log.error("Error converting message [${message}] ${e.message}")
            throw e
        }

        jmsHandler = this.handlerDelegates.get(messageObject.javaClass)

        if (jmsHandler == null) {
            throw HandlingException("No delegate for message object type [%s]".format(messageObject.javaClass, Message::class.java))
        }

        // Prepare reply channel if applicable
        var replyChannel: JmsClient? = null
        if (message.jmsReplyTo != null) {
            replyChannel = this.client.createReplyClient(session, message.jmsReplyTo)
        }

        // Delegate to handler
        try {
            jmsHandler.onMessage(messageObject, replyChannel)
        } finally {
            if (replyChannel != null)
                replyChannel.close()
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
    fun <T> addDelegate(delegate: JmsHandler<T>) {
        var messageTypes = delegate.messageTypes
        if (messageTypes.isEmpty()) {
            // No message types specified, extracing generic type from Handler interface
            val handlerInterfaces = TypeToken.of(delegate.javaClass).types.interfaces()

            // Find interface type for Handler<T> in hierarchy
            val handlerInterface = handlerInterfaces
                    .filter { it.isSubtypeOf(JmsHandler::class.java) }
                    .first()

            val handlerInterfaceType = handlerInterface.type
            if (!(handlerInterfaceType is ParameterizedType))
                throw IllegalArgumentException()

            messageTypes = arrayListOf(handlerInterfaceType.actualTypeArguments.get(0) as Class<*>)
        }

        for (c in messageTypes) {
            if (this.handlerDelegates.containsKey(c))
                throw IllegalStateException("Listener already contains handler for message type [${c.name}]")

            log.debug("Registering message handler [${delegate.javaClass.name}] for [${c.name}]")

            // Register with (all) Serializer(s)
            Serializer.types.register(c)

            handlerDelegates.put(c, delegate as JmsHandler<Any?>)
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
