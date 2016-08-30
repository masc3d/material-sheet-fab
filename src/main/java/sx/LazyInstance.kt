package sx

import java.util.*
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Supplier

/**
 * Lazy instance supporting external injection.
 * Created by masc on 14.04.15.
 */
class LazyInstance<T>
{
    private var supplier: (() -> T)?
    @Volatile private var lock: ReentrantLock?
    @Volatile private var instance: Optional<T>? = null

    companion object {
        private val DEFAULT_THREADSAFETY = ThreadSafetyMode.Synchronized
    }

    /**
     * Threadsafe modes
     */
    enum class ThreadSafetyMode {
        None,
        Synchronized
    }

    /**
     * Designated c'tor
     * @param supplier Supplier
     * @param threadSafetyMode Thread safety mode
     */
    @JvmOverloads constructor(supplier: (() -> T)? = null,
                              threadSafetyMode: LazyInstance.ThreadSafetyMode = LazyInstance.DEFAULT_THREADSAFETY) {
        this.supplier = supplier
        when (threadSafetyMode) {
            LazyInstance.ThreadSafetyMode.Synchronized -> this.lock = ReentrantLock()
            LazyInstance.ThreadSafetyMode.None -> this.lock = null
            else -> throw UnsupportedOperationException(String.format("Unsupported thread safety mode [%s]", threadSafetyMode))
        }
    }

    /**
     * C'tor
     * @param threadSafetyMode Thread safe mode
     */
    constructor(threadSafetyMode: ThreadSafetyMode) : this(null, threadSafetyMode) { }

    // TODO: set method should be migrated to reset overload also resetting the instnace
    /**
     * Set instance
     * @param supplier Supplier
     * @param ignoreExisting Does not throw if instance is already set
     */
    @JvmOverloads fun set(supplier: () -> T, ignoreExisting: Boolean = false) {
        this.lock?.lock()
        try {
            if (!ignoreExisting && instance != null)
                throw IllegalStateException("Instance already set")

            this.supplier = supplier
        } finally {
            this.lock?.unlock()
        }
    }

    /**
     * Get instance
     */
    fun get(): T {
        return this.get(this.supplier!!)
    }

    /**
     * Get instance using supplier passed with c'tor
     * @return instance
     */
    operator fun get(supplier: () -> T): T {
        if (instance == null) {
            this.lock?.lock()
            try {
                this.supplier = supplier
                this.instance = Optional.ofNullable(supplier())
            } finally {
                this.lock?.unlock()
            }
        }
        return this.instance!!.orElse(null)
    }

    /**
     * Indicates if instance was set
     * @return Instance has been set or not
     */
    val isSet: Boolean
        get() = this.instance != null

    /**
     * Conditional execution if instance is set
     * @param action Action to perform
     */
    fun ifSet(action: (T) -> Unit) {
        val instance = this.instance
        if (instance != null)
            action(instance.get())
    }

    /**
     * Reset instance
     */
    fun reset() {
        this.instance = null
    }
}