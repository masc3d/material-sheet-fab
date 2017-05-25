package sx.mq.jms

import sx.mq.MqHandler
import sx.mq.MqListener
import java.util.*
import javax.jms.ExceptionListener
import javax.jms.JMSException
import javax.jms.Message
import javax.jms.Session

/**
 * Lightweight jms message listener abstraction with object conversion and dispatch support
 * One or more object message handlers can be registered with the listener, converted messages will be dispatched by type (class)
 * Created by masc on 16.04.15.
 * @property endpoint Messaging channel
 * @property executor Thread executor
 */
abstract class JmsListener(
        /** Connection factory  */
        val endpoint: JmsEndpoint)
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
        this.endpoint.context ?: throw IllegalStateException("Listener channel requires context")
    }

    /**
     * Messaging channel
     */
    private val client by lazy {
        JmsChannel(endpoint)
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
        val converter = this.endpoint.converter

        try {
            messageObject = converter.fromMessage(message)
        } catch(e: Exception) {
            log.error("Error converting message [${message}] ${e.message}")
            throw e
        }

        // Prepare reply channel if applicable
        var replyChannel: JmsChannel? = null
        if (message.jmsReplyTo != null) {
            replyChannel = this.client.createReplyClient(session, message.jmsReplyTo)
        }

        this.handleMessage(messageObject, replyChannel)
    }

    final override fun onException(e: JMSException) {
        this.onError(e)
    }
}
