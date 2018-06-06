package sx.mq

import io.reactivex.Completable
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

    @Throws(TimeoutException::class)
    fun sendAsync(message: Any): Completable

    @Throws(TimeoutException::class)
    fun send(message: Any) = this.sendAsync(message).blockingAwait()
}