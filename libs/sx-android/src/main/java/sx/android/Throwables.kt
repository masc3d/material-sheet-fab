package sx.android

import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * Created by masc on 31.07.17.
 */
val Throwable.isConnectivityException: Boolean
    get() {
        return when(this) {
            is SocketTimeoutException,
            is ConnectException -> true
            else -> {
                when (this.cause) {
                    is SocketTimeoutException,
                    is ConnectException -> true
                    else -> false
                }
            }
        }
    }