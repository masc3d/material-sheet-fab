package sx

/**
 * Created by masc on 24/03/16.
 */
interface Lifecycle : Disposable {
    fun start()
    fun stop()
    fun restart()
    fun isRunning(): Boolean

    /** Default implementation calls stop */
    override fun close() {
        this.stop()
    }
}