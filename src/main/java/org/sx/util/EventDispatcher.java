package org.sx.util;

import java.util.ArrayList;
import java.util.EventObject;

/**
 * Multicast event dispatcher abstract and factory.
 *
 * This class is supposed to be used by observable classes, which can in turn expose instance(s) via EventDelegate interface for consumer to add listeners.
 * Created by masc on 11.08.14.
 */
public abstract class EventDispatcher<T extends EventObject> implements EventDelegate<T> {
    public EventDispatcher(){
    }

    public abstract void add(EventListener<T> listener);
    public abstract void remove(EventListener<T> listener);
    public abstract void fire(T event);

    /**
     * Factory method to create a regular (thread-unsafe) event dispatcher
     * @param <V> Type of event to dispatch
     * @return Event dispatcher
     */
    public static <V extends EventObject> EventDispatcher<V> create() {
        return new RegularEventDispatcher<V>();
    }

    /**
     * Factory method to create a thread-safe event dispatcher
     * @param <V> Type of event to dispatch
     * @return Event dispatcher
     */
    public static <V extends EventObject> EventDispatcher<V> createThreadSafe() { return new ThreadSafeEventDispatcher<V>(); }
}
