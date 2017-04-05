package sx.concurrent

import java.util.concurrent.TimeUnit

interface Event {
    fun set()
    fun reset()
    @Throws(InterruptedException::class)
    fun waitOne()

    @Throws(InterruptedException::class)
    fun waitOne(timeout: Int, unit: TimeUnit): Boolean

    val isSignalled: Boolean
}