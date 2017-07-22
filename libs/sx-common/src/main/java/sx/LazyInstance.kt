package sx

import java.util.*
import java.util.concurrent.locks.ReentrantLock

/**
 * Lazy instance supporting external injection.
 * Created by masc on 14.04.15.
 */
class LazyInstance<T>
{
    private class Instance<T>(val value: T)

    private var supplier: (() -> T)?
    @Volatile private var lock: ReentrantLock?
    @Volatile private var instance: Instance<T>? = null

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
     * Lock helper
     */
    private fun withLock(block: () -> Unit) {
        this.lock?.lock()
        try {
            block()
        } finally {
            this.lock?.unlock()
        }
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
        }
    }

    /**
     * C'tor
     * @param threadSafetyMode Thread safe mode
     */
    constructor(threadSafetyMode: ThreadSafetyMode) : this(null, threadSafetyMode)

    /**
     * Reset on condition
     * @param predicate Condition
     * @param supplier (Optional) supplier to (re)set to
     */
    fun resetIf(predicate: ((T) -> Boolean)? = null, supplier: (() -> T)? = null) {
        // If supplier won't change and instance hasn't been initialized, avoid lock & return immediately
        if (supplier == null && !isSet)
            return

        this.withLock {
            if (predicate == null || predicate(this.get())) {
                if (supplier != null) {
                    this.supplier = supplier
                }
                this.instance = null
            }
        }
    }

    /**
     * Reset on condition
     * @param predicate Condition
     * @param value Value
     */
    fun resetIf(predicate: ((T) -> Boolean)? = null, value: T) {
        this.resetIf(predicate, { value })
    }

    /**
     * Reset
     * @param supplier Supplier
     */
    fun reset(supplier: (() -> T)? = null) {
        this.resetIf(null, supplier)
    }

    /**
     * Reset
     *
     */
    fun reset(value: T) {
        this.resetIf(null, value)
    }
    /**
     * Set instance (deferred)
     * This method throws when the instance has already been set internally (eg. by a consumer requesting it)
     * @param supplier Supplier
     */
    fun set(supplier: () -> T) {
        this.withLock {
            if (instance != null)
                throw IllegalStateException("Instance already set")

            this.supplier = supplier
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
            this.withLock {
                this.supplier = supplier
                this.instance = Instance(value = supplier())
            }
        }
        return this.instance!!.value
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
            action(instance.value)
    }
}