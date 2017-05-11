package sx.mq

/**
 * JMS message handler
 * Created by masc on 28.06.15.
 */
interface MqHandler<in T> {
    /** Message types to handle. Must be overridden when multiple message types should be handled (Handler<Anx>) */
    val messageTypes: List<Class<*>> get() {
        return arrayListOf()
    }

    /** Override for serialized/object message handling */
    fun onMessage(message: T, replyClient: MqClient?)
}
