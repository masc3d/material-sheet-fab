package sx.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Multicast event dispatcher abstract and factory.
 *
 * This class is supposed to be used by observable classes, which can in turn expose instance(s) via EventDelegate interface for consumer to add listeners.
 * Created by masc on 11.08.14.
 */
public abstract class EventDispatcher<T extends EventListener> implements EventDelegate<T> {
    /**
     * Generic runnable interface for event emission
     * @param <T>
     */
    public interface Runnable<T> {
        public void run(T listener);
    }

    public EventDispatcher(){
    }

    /**
     * Add listener
     * @param listener
     */
    public abstract void add(T listener);
    /**
     * Remove listener
     * @param listener
     */
    public abstract void remove(T listener);
    /**
     * Remove weak references from listeners, used for cleaning out garbage collected/invalid rfs
     * @param listeners
     */
    protected abstract void remove(List<WeakReference<T>> listeners);
    /**
     * Get copy of listener references
     */
    protected abstract List<WeakReference<T>> getListeners();
    /**
     * Emit event to listeners
     * @param r Runnable supposed to call listener interface method
     */
    public void emit(Runnable<T> r) {
        List<WeakReference<T>> invalidRefs = null;

        List<WeakReference<T>> listenerRefs = this.getListeners();
        for (WeakReference<T> listener : listenerRefs) {
            T instance = listener.get();
            if (instance != null)
                r.run(instance);
            else {
                if (invalidRefs == null)
                    invalidRefs = new ArrayList<WeakReference<T>>();
                invalidRefs.add(listener);
            }
        }

        // Remove invalid refs
        if (invalidRefs != null)
            this.remove(invalidRefs);
    }
    /**
     * Factory method to create a regular (thread-unsafe) event dispatcher
     * @param <V> Type of event to dispatch
     * @return Event dispatcher
     */
    public static <V extends EventListener> EventDispatcher<V> create() {
        return new RegularEventDispatcher<V>();
    }

    /**
     * Factory method to create a thread-safe event dispatcher
     * @param <V> Type of event to dispatch
     * @return Event dispatcher
     */
    public static <V extends EventListener> EventDispatcher<V> createThreadSafe() { return new ThreadSafeEventDispatcher<V>(); }
}
