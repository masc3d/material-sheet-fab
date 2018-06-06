package sx.mq

import kotlin.reflect.KClass

/**
 * JMS message handler
 * Created by masc on 28.06.15.
 */
interface MqHandler<in T> {
    /** Annotation for declaring Message types to handle when using a common super type eg. `MqHandler<Any>` */
    @Target(AnnotationTarget.FUNCTION)
    annotation class Types(vararg val types: KClass<*>)

    /** Override for serialized/object message handling */
    fun onMessage(message: T, replyChannel: MqChannel?)
}
