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

/**
 * Multicast event dispatcher without thread-safety.
 * Instance methods are NOT thread-safe for performance reasons.
 * Consumers need to ensure thread safety if needed
 *
 * @author masc
 */
public class RegularEventDispatcher<T extends EventListener> extends EventDispatcher<T> {
    private final ArrayList<T> _listeners = new ArrayList<T>();

    @Override
    public void add(T listener) {
        _listeners.add(listener);
    }

    @Override
    public void remove(T listener) {
        _listeners.remove(listener);
    }

    @Override
    public void emit(Runnable<T> r) {
        // Also non-threadsafe dispatcher needs a copy of listeners,
        // as consumers may modify the collection by adding/removing listeners
        ArrayList<T> listeners = new ArrayList<T>(_listeners);
        for (T listener : listeners)
            r.run(listener);
    }
}
