package sx;

import java.util.function.Supplier;

/**
 * Lazy instance
 * Created by masc on 14.04.15.
 */
public class LazyInstance<T> {
    final Object mLock = new Object();
    Supplier<T> mSupplier;
    volatile T mInstance = null;

    public LazyInstance(Supplier<T> supplier) {
        mSupplier = supplier;
    }

    public LazyInstance() {
        this(null);
    }

    /**
     * Set instance
     * @param supplier
     * @param ignoreExisting Does not throw if instance is already set
     */
    public void set(Supplier<T> supplier, boolean ignoreExisting) {
        synchronized (mLock) {
            if (!ignoreExisting && mInstance != null)
                throw new IllegalStateException("Instance already set");
            mSupplier = supplier;
        }
    }

    /**
     * Set instance
     * @param supplier
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
            synchronized (mLock) {
                mSupplier = supplier;
                if (mSupplier != null)
                    mInstance = mSupplier.get();
            }
        }
        return mInstance;
    }
}