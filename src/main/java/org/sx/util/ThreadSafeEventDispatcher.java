package org.sx.util;

import java.util.ArrayList;

/**
 * Threadsafe multicast event dispatcher.
 *
 * @author masc
 */
public class ThreadSafeEventDispatcher<T extends EventListener> extends RegularEventDispatcher<T> {
    @Override
    public void add(T listener) {
        synchronized (_listeners) {
            super.add(listener);
        }
    }

    @Override
    public void remove(T listener) {
        synchronized (_listeners) {
            super.remove(listener);
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
