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

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.lang.ref.WeakReference;
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
    public void add(T listener) {
        _listeners.add(new ListenerReference(listener, (Class<T>)listener.getClass()));
    }

    @Override
    public void remove(final T listener) {
        ArrayList toRemove = Lists.newArrayList(Collections2.filter(_listeners, new Predicate<WeakReference<T>>() {
            @Override
            public boolean apply(WeakReference<T> input) {
                return input.get() == listener;
            }
        }));
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
