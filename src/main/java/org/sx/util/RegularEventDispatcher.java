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
 * Multicast event dispatcher without thread-safety.
 * Instance methods are NOT thread-safe for performance reasons.
 * Consumers need to ensure thread safety if needed
 *
 * @author masc
 */
public class RegularEventDispatcher<T extends EventObject> extends EventDispatcher<T> {
    private final ArrayList<EventListener<T>> _listeners = new ArrayList<EventListener<T>>();

    @Override
    public void add(EventListener<T> listener) {
        _listeners.add(listener);
    }

    @Override
    public void remove(EventListener<T> listener) {
        _listeners.remove(listener);
    }

    @Override
    public void fire(T event) {
        for (EventListener<T> listener : _listeners)
            listener.handle(event);
    }
}
