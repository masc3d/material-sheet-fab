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

package sx.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Multicast event dispatcher without thread-safety.
 * Instance methods are NOT thread-safe for performance reasons.
 * Consumers need to ensure thread safety if needed
 *
 * @author masc
 */
public class RegularEventDispatcher<T extends EventListener> extends EventDispatcher<T> {
    /**
     * Weak references of listeners
     * TODO: Use a hashed type of list eg. LinkedHashSet or possibly a hashset which allows duplicates
     * for better performance especially when removing listeners (there may be a lot)
     */
    private final ArrayList<ListenerReference> _listeners = new ArrayList<ListenerReference>();

    @Override
    @SuppressWarnings("unchecked")
    public void add(T listener) {
        _listeners.add(new ListenerReference(listener, (Class<T>)listener.getClass()));
    }

    @Override
    public void remove(final T listener) {
        ArrayList<ListenerReference> toRemove = new ArrayList<>();

        for (ListenerReference l : _listeners) {
            if (l.get() == listener) {
                toRemove.add(l);
            }
        }

        _listeners.removeAll(toRemove);
    }

    @Override
    protected void remove(List<ListenerReference> listeners) {
        _listeners.removeAll(listeners);
    }

    @Override
    protected List<ListenerReference> getListeners() {
        return new ArrayList<ListenerReference>(_listeners);
    }
}
