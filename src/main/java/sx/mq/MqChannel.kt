package sx.mq

import sx.Disposable
import java.io.Closeable
import java.util.concurrent.TimeoutException

/**
 * Abstract messaging channel
 * Created by masc on 07.05.17.
 */
interface MqChannel : Disposable, Closeable {
    @Throws(TimeoutException::class)
    fun <T> receive(messageType: Class<T>): T
    fun send(message: Any)
}