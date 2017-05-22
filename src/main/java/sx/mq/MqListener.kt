package sx.mq

import org.slf4j.LoggerFactory
import sx.Disposable
import sx.io.serialization.Serializer
import sx.reflect.allGenericInterfaces
import java.lang.reflect.ParameterizedType
import java.util.*
import javax.jms.Message

/**
 * Lightweight message listener abstraction with object conversion and dispatch support
 * One or more object message handlers can be registered with the listener, converted messages will be dispatched by type (class)
 * Created by masc on 16.04.15.
 * @property channel Messaging channel
 * @property executor Thread executor
 */
abstract class MqListener
    :
        Disposable {
    protected val log = LoggerFactory.getLogger(this.javaClass)
    /** Object message handler delegates  */
    private val handlerDelegates = HashMap<Class<*>, MqHandler<Any?>>()

    /**
     * Message handling exception
     */
    private inner class HandlingException(message: String) : RuntimeException(message)

    abstract fun start()

    abstract fun stop()

    protected fun handleMessage(messageObject: Any, replyClient: MqClient? = null) {
        val mqHandler: MqHandler<*>?

        mqHandler = this.handlerDelegates.get(messageObject.javaClass)

        if (mqHandler == null) {
            throw HandlingException("No delegate for message object type [%s]".format(messageObject.javaClass, Message::class.java))
        }

        // Delegate to handler
        try {
            mqHandler.onMessage(messageObject, replyClient)
        } finally {
            if (replyClient != null)
                replyClient.close()
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
    fun <T> addDelegate(delegate: MqHandler<T>) {
        val messageTypes = arrayListOf<Class<*>>()

        // Lookup MqHandler interface
        val handlerInterface = delegate.javaClass.allGenericInterfaces
                .first {
                    it is ParameterizedType && it.rawType == MqHandler::class.java
                } as ParameterizedType

        // The generic interface parameter (=message type)
        val handlerMessageType = handlerInterface.actualTypeArguments.get(0) as Class<*>

        // Locate interface method for checking types annotation
        val interfaceType = handlerInterface.rawType as Class<*>
        interfaceType.methods.forEach { interfaceMethod ->
            val method = delegate.javaClass.getMethod(interfaceMethod.name, *interfaceMethod.parameterTypes)

            val typesAnnotation = method.getAnnotation(MqHandler.Types::class.java)
            if (typesAnnotation != null) {
                messageTypes.addAll(typesAnnotation.types.map { it.java })
            }
        }

        if (messageTypes.isEmpty()) {
            // No message types specified, using generic type from Handler interface (except for Object/Any)
            if (handlerMessageType != Any::class.java)
                messageTypes.add(handlerMessageType)
        }

        if (messageTypes.isEmpty()) {
            log.warn("Message delegate [${delegate.javaClass}] does not define any relevant message type")
        }

        messageTypes.forEach { c ->
            if (this.handlerDelegates.containsKey(c))
                throw IllegalStateException("Listener already contains handler for message type [${c.name}]")

            log.debug("Registering message handler [${delegate.javaClass.name}] for [${c.name}]")

            // Register with (all) Serializer(s)
            Serializer.types.register(c)

            handlerDelegates.put(c, delegate as MqHandler<Any?>)
        }
    }

    override fun close() {
        // Unlike calling .stop directly, .close shouldn't throw
        try {
            this.stop()
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }
}
