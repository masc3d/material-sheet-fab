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
     * Get instance using supplier passed with c'tor
     *
     * @return instance
     */
    public T get() {
        return this.get(mSupplier);
    }

    public void set(T instance) {
        synchronized (mLock) {
            mInstance = instance;
        }
    }

    /**
     * Get instance
     *
     * @param supplier Supplier to provide instance
     * @return Instance
     */
    public T get(Supplier<T> supplier) {
        if (mInstance == null) {
            synchronized (mLock) {
                if (supplier != null)
                    mInstance = supplier.get();
            }
        }
        return mInstance;
    }
}