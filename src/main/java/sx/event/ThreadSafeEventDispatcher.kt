package sx.event

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Threadsafe multicast event dispatcher.

 * @author masc
 */
class ThreadSafeEventDispatcher<T : EventListener> : RegularEventDispatcher<T>() {
    private val lock = ReentrantLock()

    override fun add(listener: T) {
        this.lock.withLock {
            super.add(listener)
        }
    }

    override fun remove(listener: T) {
        this.lock.withLock {
            super.remove(listener)
        }
    }

    override val listeners: List<ListenerReference>
    get() {
        this.lock.withLock {
            return super.listeners
        }
    }

    override fun remove(listeners: List<ListenerReference>) {
        this.lock.withLock {
            super.remove(listeners)
        }
    }
}
