package sx.jms

import com.google.common.reflect.TypeToken
import java.io.Serializable
import java.lang.reflect.ParameterizedType

/**
 * Message handler
 * Created by masc on 28.06.15.
 */
interface Handler<in T> {
    /** Message types to handle. Must be overridden when multiple message types should be handled (Handler<Anx>) */
    val messageTypes: List<Class<*>> get() {
        return arrayListOf()
    }

    /** Override for serialized/object message handling */
    fun onMessage(message: T, replyChannel: Channel?)
}
