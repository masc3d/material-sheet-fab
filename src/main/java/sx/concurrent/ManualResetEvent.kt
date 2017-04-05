package sx.concurrent

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Manual reset mLatch
 * @author masc
 */
class ManualResetEvent(signalled: Boolean) : Event {
    @Volatile private var latch: CountDownLatch
    private val sync = ReentrantLock()

    init {
        if (signalled) {
            latch = CountDownLatch(0)
        } else {
            latch = CountDownLatch(1)
        }
    }

    override fun set() {
        latch.countDown()
    }

    override fun reset() {
        this.sync.withLock {
            if (latch.count == 0L) {
                latch = CountDownLatch(1)
            }
        }
    }

    @Throws(InterruptedException::class)
    override fun waitOne() {
        latch.await()
    }

    @Throws(InterruptedException::class)
    override fun waitOne(timeout: Int, unit: TimeUnit): Boolean {
        return latch.await(timeout.toLong(), unit)
    }

    override val isSignalled: Boolean
        get() = (latch.count == 0L)

}