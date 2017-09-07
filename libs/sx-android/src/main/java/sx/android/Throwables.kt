package sx.android

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Indicates if this or the causing exception relates to a connectivity problem
 * Created by masc on 31.07.17.
 */
val Throwable.isConnectivityProblem: Boolean
    get() {
        val connectivityExceptions = listOf(
                SocketTimeoutException::class.java,
                UnknownHostException::class.java,
                ConnectException::class.java
        )

        return connectivityExceptions.any {
            it.isInstance(this) ||
                    it.isInstance(this.cause)
        }
    }