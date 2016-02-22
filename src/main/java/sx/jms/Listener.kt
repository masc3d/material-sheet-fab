package sx.jms

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import sx.Disposable
import java.util.*
import javax.jms.*

/**
 * Lightweight jms message listener abstraction.
 * This is the top level abstract class, only binding a connection factory.
 * Created by masc on 16.04.15.
 * @property converter Message converter
 */
abstract class Listener(
        /** Connection factory  */
        protected val connectionFactory: ConnectionFactory,
        protected val converter: Converter? = null)
:
        Disposable,
        ExceptionListener {
    protected val log: Log = LogFactory.getLog(this.javaClass)
    /** Object message handler delegates  */
    private val handlerDelegates = HashMap<Class<out Any?>, Handler<Any?>>()

    /**
     * Message handling exception
     */
    private inner class HandlingException(message: String) : RuntimeException(message)

    @Throws(JMSException::class)
    abstract fun start()

    @Throws(JMSException::class)
    abstract fun stop()

    /**
     * Default message handler with support for object message delegates
     * @param message
     * @param session
     * @throws JMSException
     */
    @Throws(JMSException::class)
    protected open fun onMessage(message: Message, session: Session) {
        var messageObject: Any? = null

        var handler: Handler<Any?>? = null

        if (this.converter != null) {
            messageObject = this.converter.fromMessage(message)
            handler = this.handlerDelegates.getOrDefault(messageObject.javaClass, null)
        }

        if (handler == null) {
            if (messageObject != null)
                throw HandlingException("No delegate for message object type [%s]".format(messageObject.javaClass, Message::class.java))

            throw HandlingException("No message converter nor generic delegate for jms messages")
        }

        if (messageObject != null)
            handler.onMessage(messageObject, converter!!, message, session, this.connectionFactory)
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
     * @param c Class of object/message to process
     * @param delegate Handler
     * @param  Type of object/message to process
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> addDelegate(c: Class<T>, delegate: Handler<T>) {
        handlerDelegates.put(c, delegate as Handler<Any?>)
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
