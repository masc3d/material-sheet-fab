package org.sx.util;

import java.util.EventObject;

/**
 * Multicast event delegate interface
 * Created by masc on 11.08.14.
 */
public interface EventDelegate<T extends EventObject> {
    public void add(EventListener<T> listener);
    public void remove(EventListener<T> listener);
}
