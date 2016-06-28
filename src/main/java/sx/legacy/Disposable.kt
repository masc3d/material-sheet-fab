package sx.legacy

import java.io.Closeable

/**
 * Legacy version of Disposable interface, compatible with 1.6 for older jdk/android support
 * Created by masc on 03/03/16.
 */
interface Disposable : Closeable {
}