package sx.event

import org.slf4j.LoggerFactory

import java.lang.ref.WeakReference
import java.util.ArrayList

/**
 * Multicast event dispatcher abstract and factory.
 *
 *
 * This class is supposed to be used by observable classes, which can in turn expose instance(s) via EventDelegate interface for consumer to add listeners.
 * Created by masc on 11.08.14.
 */
abstract class EventDispatcher<T : EventListener> : EventDelegate<T> {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Listener reference with type information
     */
    inner class ListenerReference(referent: T, c: Class<T>) : WeakReference<T>(referent) {
        val listenerClass: Class<*>

        init {
            listenerClass = c
        }
    }

    /**
     * Add listener
     * @param listener
     */
    abstract override fun add(listener: T)

    /**
     * Remove listener
     * @param listener
     */
    abstract override fun remove(listener: T)

    /**
     * Remove weak references from listeners, used for cleaning out garbage collected/invalid rfs
     * @param listeners
     */
    protected abstract fun remove(listeners: List<ListenerReference>)

    /**
     * Get copy of listener references
     */
    protected abstract val listeners: List<ListenerReference>

    /**
     * Emit event to listeners
     * @param r Runnable supposed to call listener interface method
     */
    fun emit(r: (listener: T) -> Unit) {
        var invalidRefs: MutableList<ListenerReference>? = null

        val listenerRefs = this.listeners
        for (listener in listenerRefs) {
            val instance = listener.get()
            if (instance != null)
                r(instance)
            else {
                if (invalidRefs == null)
                    invalidRefs = ArrayList<ListenerReference>()
                invalidRefs.add(listener)
            }
        }

        // Remove invalid refs
        if (invalidRefs != null) {
            for (ir in invalidRefs) {
                // Convert interface class instances to strings (for logging)
                val interfaceListText = ir.listenerClass.interfaces.map { "${it}" }.joinToString(",")

                log.info("Removing gc'ed listener ${ir.listenerClass} (${interfaceListText})")
            }
            this.remove(invalidRefs)
        }
    }

    companion object {
        /**
         * Factory method to create a regular (thread-unsafe) event dispatcher
         * @param <V> Type of event to dispatch
         * @return Event dispatcher
         */
        @JvmStatic fun <V : EventListener> create(): EventDispatcher<V> {
            return RegularEventDispatcher()
        }

        /**
         * Factory method to create a thread-safe event dispatcher
         * @param <V> Type of event to dispatch
         * @return Event dispatcher
         */
        @JvmStatic fun <V : EventListener> createThreadSafe(): EventDispatcher<V> {
            return ThreadSafeEventDispatcher()
        }
    }
}
