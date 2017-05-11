package sx.mq.jms

import com.google.common.reflect.TypeToken
import org.slf4j.LoggerFactory
import sx.Disposable
import sx.io.serialization.Serializer
import sx.mq.MqHandler
import sx.mq.MqListener
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
        MqListener(),
        ExceptionListener {
    /** Object message handler delegates  */
    private val handlerDelegates = HashMap<Class<*>, MqHandler<Any?>>()

    /**
     * Message handling exception
     */
    private inner class HandlingException(message: String) : RuntimeException(message)

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

        // Deserialize if there's a converter and determine handler
        val converter = this.channel.converter

        try {
            messageObject = converter.fromMessage(message)
        } catch(e: Exception) {
            log.error("Error converting message [${message}] ${e.message}")
            throw e
        }

        // Prepare reply channel if applicable
        var replyClient: JmsClient? = null
        if (message.jmsReplyTo != null) {
            replyClient = this.client.createReplyClient(session, message.jmsReplyTo)
        }

        this.handleMessage(messageObject, replyClient)
    }

    final override fun onException(e: JMSException) {
        this.onError(e)
    }
}
