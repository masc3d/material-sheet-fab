package sx;

import java.util.function.Supplier;

/**
 * Lazy instance
 * Created by masc on 14.04.15.
 */
public class LazyInstance<T> {
    final Object mLock = new Object();
    Supplier<T> mSupplier;
    T instance = null;

    public LazyInstance(Supplier<T> supplier) {
        mSupplier = supplier;
    }
    public LazyInstance() {
        this( null );
    }

    /**
     * Get instance using supplier passed with c'tor
     * @return instance
     */
    public T get() {
        return this.get(mSupplier);
    }

    /**
     * Get instance
     * @param supplier Supplier to provide instance
     * @return Instance
     */
    public T get(Supplier<T> supplier) {
        if (instance == null) {
            synchronized ( mLock ) {
                instance = supplier.get();
            }
        }
        return instance;
    }
}