package sx.util;

/**
 * Multicast event delegate interface
 * Created by masc on 11.08.14.
 */
public interface EventDelegate<T extends EventListener> {
    public void add(T listener);
    public void remove(T listener);
}
