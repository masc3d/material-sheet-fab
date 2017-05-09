package sx.mq

import sx.Disposable
import sx.io.serialization.Serializer
import java.io.Closeable
import java.util.concurrent.TimeoutException

/**
 * Abstract messaging channel
 * Created by masc on 07.05.17.
 */
interface Client : Disposable, Closeable {
    @Throws(TimeoutException::class)
    fun <T> receive(messageType: Class<T>): T
    fun send(message: Any)
}