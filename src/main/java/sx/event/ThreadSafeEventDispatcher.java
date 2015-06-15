package sx.event;

import java.util.List;

/**
 * Threadsafe multicast event dispatcher.
 *
 * @author masc
 */
public class ThreadSafeEventDispatcher<T extends EventListener> extends RegularEventDispatcher<T> {
    @Override
    public synchronized void add(T listener) {
        super.add(listener);
    }

    @Override
    public synchronized void remove(T listener) {
        super.remove(listener);
    }

    @Override
    protected synchronized List<ListenerReference> getListeners() {
        return super.getListeners();
    }

    @Override
    protected synchronized void remove(List<ListenerReference> listeners) {
        super.remove(listeners);
    }
}
