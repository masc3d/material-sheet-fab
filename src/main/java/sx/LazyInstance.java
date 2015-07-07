package sx;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Lazy instance
 * Created by masc on 14.04.15.
 */
public class LazyInstance<T> {
    private final Object mLock = new Object();
    private Supplier<T> mSupplier;
    private volatile Optional<T> mInstance = null;

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
                mInstance = Optional.ofNullable(mSupplier.get());
            }
        }
        return mInstance.orElse(null);
    }

    /**
     * Indicates if instance was set
     * @return
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
}