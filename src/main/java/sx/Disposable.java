package sx;

/**
 * Created by masc on 23.09.14.
 */
public interface Disposable extends AutoCloseable {
    void close();
}
