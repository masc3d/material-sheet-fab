package org.sx.util;

import java.util.ArrayList;
import java.util.EventObject;

/**
 * Threadsafe multicast event dispatcher.
 *
 * @author masc
 */
public class ThreadSafeEventDispatcher<T extends EventObject> extends EventDispatcher<T> {
    private final ArrayList<EventListener<T>> _listeners = new ArrayList<EventListener<T>>();

    @Override
    public void add(EventListener<T> listener) {
        synchronized (_listeners) {
            _listeners.add(listener);
        }
    }

    @Override
    public void remove(EventListener<T> listener) {
        synchronized (_listeners) {
            _listeners.remove(listener);
        }
    }

    @Override
    public void fire(T event) {
        ArrayList<EventListener<T>> listeners;
        synchronized (_listeners) {
            listeners = new ArrayList<EventListener<T>>(_listeners);
        }
        for (EventListener<T> listener : listeners)
            listener.handle(event);
    }
}
