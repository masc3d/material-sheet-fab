package sx.concurrent

import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

/**
 * Automatic reset mSemaphore
 * @author masc
 */
class AutoResetEvent(signalled: Boolean) : Event {
    private val semaphore: Semaphore

    init {
        semaphore = Semaphore(if (signalled) 1 else 0)
    }

    override fun set() {
        synchronized (semaphore) {
            if (semaphore.availablePermits() == 0)
                semaphore.release()
        }
    }

    override fun reset() {
        semaphore.drainPermits()
    }

    @Throws(InterruptedException::class)
    override fun waitOne() {
        semaphore.acquire()
    }

    @Throws(InterruptedException::class)
    override fun waitOne(timeout: Int, unit: TimeUnit): Boolean {
        return semaphore.tryAcquire(timeout.toLong(), unit)
    }

    override val isSignalled: Boolean
        get() = semaphore.availablePermits() > 0
}