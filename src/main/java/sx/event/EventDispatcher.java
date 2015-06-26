package sx.event;

import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Multicast event dispatcher abstract and factory.
 * <p/>
 * This class is supposed to be used by observable classes, which can in turn expose instance(s) via EventDelegate interface for consumer to add listeners.
 * Created by masc on 11.08.14.
 */
public abstract class EventDispatcher<T extends EventListener> implements EventDelegate<T> {
    private Log mLog = LogFactory.getLog(this.getClass());

    /** Listener reference with type information */
    class ListenerReference extends WeakReference<T> {
        private Class mClass;
        public ListenerReference(T referent, Class c) {
            super(referent);
            mClass = c;
        }

        public Class getListenerClass() {
            return mClass;
        }
    }

    /**
     * Generic runnable interface for event emission
     *
     * @param <T>
     */
    public interface Runnable<T> {
        public void run(T listener);
    }

    public EventDispatcher() {
    }

    /**
     * Add listener
     *
     * @param listener
     */
    public abstract void add(T listener);
    /**
     * Remove listener
     *
     * @param listener
     */
    public abstract void remove(T listener);
    /**
     * Remove weak references from listeners, used for cleaning out garbage collected/invalid rfs
     *
     * @param listeners
     */
    protected abstract void remove(List<ListenerReference> listeners);
    /**
     * Get copy of listener references
     */
    protected abstract List<ListenerReference> getListeners();

    /**
     * Emit event to listeners
     *
     * @param r Runnable supposed to call listener interface method
     */
    public void emit(Runnable<T> r) {
        List<ListenerReference> invalidRefs = null;

        List<ListenerReference> listenerRefs = this.getListeners();
        for (ListenerReference listener : listenerRefs) {
            T instance = listener.get();
            if (instance != null)
                r.run(instance);
            else {
                if (invalidRefs == null)
                    invalidRefs = new ArrayList<ListenerReference>();
                invalidRefs.add(listener);
            }
        }

        // Remove invalid refs
            if (invalidRefs != null) {
                for (ListenerReference ir : invalidRefs) {
                    mLog.info(String.format("Removing gc'ed listener %s (%s)",
                            ir.getListenerClass(),
                            String.join(",",
                                    (Iterable)Lists.newArrayList(ir.getListenerClass().getInterfaces()).stream().map(c -> c.toString())::iterator
                            )));
                }
                this.remove(invalidRefs);
            }
    }

    /**
     * Factory method to create a regular (thread-unsafe) event dispatcher
     *
     * @param <V> Type of event to dispatch
     * @return Event dispatcher
     */
    public static <V extends EventListener> EventDispatcher<V> create() {
        return new RegularEventDispatcher<V>();
    }

    /**
     * Factory method to create a thread-safe event dispatcher
     *
     * @param <V> Type of event to dispatch
     * @return Event dispatcher
     */
    public static <V extends EventListener> EventDispatcher<V> createThreadSafe() {
        return new ThreadSafeEventDispatcher<V>();
    }
}
