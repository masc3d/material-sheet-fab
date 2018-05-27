package sx

/**
 * Created by masc on 23.09.14.
 */
interface Disposable : AutoCloseable {
    override fun close()
}