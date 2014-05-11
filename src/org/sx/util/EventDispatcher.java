/*
 *  Copyright (C) 2010 masc
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sx.util;

import java.util.ArrayList;
import java.util.EventObject;

/**
 * Multicast event dispatcher. Instance methods are not threadsafe.
 *
 * @author masc
 */
public class EventDispatcher<T extends EventObject> {
    private final ArrayList<EventListener<T>> _listeners = new ArrayList<EventListener<T>>();

    public EventDispatcher() {
    }

    public void add(EventListener<T> listener) {
        _listeners.add(listener);
    }

    public void remove(EventListener<T> listener) {
        _listeners.remove(listener);
    }

    public void fire(T event) {
        ArrayList<EventListener<T>> listeners = (ArrayList<EventListener<T>>) _listeners.clone();

        for (EventListener<T> listener : listeners)
            listener.handle(event);
    }
}
