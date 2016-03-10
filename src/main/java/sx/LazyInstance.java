package sx;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * Lazy instance supporting external injection.
 * Created by masc on 14.04.15.
 */
public class LazyInstance<T> {
    private static final ThreadSafeMode DEFAULT_THREADSAFEMODE = ThreadSafeMode.Synchronized;
    private volatile ReentrantLock mLock;
    private Supplier<T> mSupplier;
    private volatile Optional<T> mInstance = null;

    /**
     * Threadsafe modes
     */
    public enum ThreadSafeMode {
        None,
        Synchronized
    }

    /**
     * Designated c'tor
     * @param supplier Supplier
     */
    public LazyInstance(Supplier<T> supplier, ThreadSafeMode threadSafeMode) {
        mSupplier = supplier;
        switch(threadSafeMode) {
            case Synchronized:
                mLock = new ReentrantLock();
                break;
            case None:
                mLock = null;
                break;
            default:
                throw new UnsupportedOperationException(String.format("Unsupported thread safety mode [%s]", threadSafeMode));
        }
    }

    /**
     * c'tor creating thread safe lazy instance
     * @param supplier Supplier
     */
    public LazyInstance(Supplier<T> supplier) {
        this(supplier, DEFAULT_THREADSAFEMODE);
    }

    /**
     * C'tor
     * @param threadSafeMode Thread safe mode
     */
    public LazyInstance(ThreadSafeMode threadSafeMode) {
        this(null, threadSafeMode);
    }

    /**
     * c'tor creating thread safe lazy instance without supplier
     */
    public LazyInstance() {
        this(null, DEFAULT_THREADSAFEMODE);
    }

    /**
     * Set instance
     * @param supplier Supplier
     * @param ignoreExisting Does not throw if instance is already set
     */
    public void set(Supplier<T> supplier, boolean ignoreExisting) {
        if (mLock != null) {
            mLock.lock();
        }
        try {
            if (!ignoreExisting && mInstance != null)
                throw new IllegalStateException("Instance already set");
            mSupplier = supplier;
        } finally {
            if (mLock != null) {
                mLock.unlock();
            }
        }
    }

    /**
     * Set instance
     * @param supplier Supplier
     */
    public void set(Supplier<T> supplier) {
        this.set(supplier, false);
    }

    /**
     * Get instance
     */
    public T get() {
        return this.get(mSupplier);
    }

    /**
     * Get instance using supplier passed with c'tor
     * @return instance
     */
    public T get(Supplier<T> supplier) {
        if (mInstance == null) {
            if (mLock != null) {
                mLock.lock();
            }
            try {
                mSupplier = supplier;
                mInstance = Optional.ofNullable(mSupplier.get());
            } finally {
                if (mLock != null) {
                    mLock.unlock();
                }
            }
        }
        return mInstance.orElse(null);
    }

    /**
     * Indicates if instance was set
     * @return Instance has been set or not
     */
    public boolean isSet() {
        return mInstance != null;
    }

    /**
     * Conditional execution if instance is set
     * @param action Action to perform
     */
    public void ifSet(Action<T> action) {
        if (this.isSet())
            action.perform(mInstance.get());
    }

    /**
     * Reset instance
     */
    public void reset() {
        mInstance = null;
    }
}