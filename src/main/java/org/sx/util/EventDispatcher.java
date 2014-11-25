package org.sx.util;

/**
 * Multicast event dispatcher abstract and factory.
 *
 * This class is supposed to be used by observable classes, which can in turn expose instance(s) via EventDelegate interface for consumer to add listeners.
 * Created by masc on 11.08.14.
 */
public abstract class EventDispatcher<T extends EventListener> implements EventDelegate<T> {
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
     * Add listener safely
     * If instance does not implement the interface this method does nothing
     * @param listener Listener to add
     */
    public abstract void add(Object listener);
    public abstract void remove(T listener);
    public abstract void emit(Runnable<T> r);

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
