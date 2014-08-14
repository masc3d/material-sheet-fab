package org.sx.util;

import java.util.ArrayList;
import java.util.EventObject;

/**
 * Threadsafe multicast event dispatcher.
 *
 * @author masc
 */
public class ThreadSafeEventDispatcher<T extends EventListener> extends EventDispatcher<T> {
    private final ArrayList<T> _listeners = new ArrayList<T>();

    @Override
    public void add(T listener) {
        synchronized (_listeners) {
            _listeners.add(listener);
        }
    }

    @Override
    public void remove(T listener) {
        synchronized (_listeners) {
            _listeners.remove(listener);
        }
    }

    @Override
    public void emit(Runnable<T> r) {
        ArrayList<T> listeners;
        synchronized (_listeners) {
            listeners = new ArrayList<T>(_listeners);
        }
        for (T listener : listeners)
            r.run(listener);
    }
}
