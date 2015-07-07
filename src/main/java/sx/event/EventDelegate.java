package sx.event;

/**
 * Multicast event delegate interface
 * Created by masc on 11.08.14.
 */
public interface EventDelegate<T extends EventListener> {
    /** Add listener to this multicast delegate */
    void add(T listener);
    /** Remove listener from this multicast delegate */
    void remove(T listener);
}
