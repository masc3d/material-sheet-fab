package sx.jms

/**
 * Message handler
 * Created by masc on 28.06.15.
 */
interface Handler<in T> {
    /** Override for serialized/object message handling */
    fun onMessage(message: T, replyChannel: Channel?)
}
