package sx

import org.slf4j.LoggerFactory

/**
 * Created by masc on 07.07.15.
 */
object Dispose {
    internal var log = LoggerFactory.getLogger(Dispose::class.java)

    /**
     * Customized disposal
     * @param r Dispose operation
     */
    @JvmOverloads fun safely(r: () -> Unit, exceptionHandler: ((Exception) -> Unit)? = null) {
        try {
            r()
        } catch (e: Exception) {
            exceptionHandler?.invoke(e)
        }
    }

    /**
     * Safely disposes an instance implementing @link Disposable, logging, timing and errors
     * @param d Disposable instance
     * @param exceptionHandler Optional exception handler
     */
    @JvmOverloads fun safely(d: Disposable, exceptionHandler: ((Exception) -> Unit)? = null) {
        log.info(String.format("Disposing [%s]", d.javaClass.name))
        val sw = Stopwatch.createStarted()
        this.safely(
                { d.close() },
                { e ->
                    log.error(e.message, e)
                    exceptionHandler?.invoke(e)
                })
        log.info(String.format("Disposed [%s] in [%s]", d.javaClass.name, sw.toString()))
    }

    /**
     * Safely disposes an instance implementing @link Disposable, logging, timing and errors
     * @param d Disposable instance
     * @param exceptionHandler Optional exception handler
     */
    @JvmOverloads fun safely(d: sx.legacy.Disposable, exceptionHandler: ((Exception) -> Unit)? = null) {
        this.safely(object : Disposable {
            override fun close() {
                d.close()
            }
        }, exceptionHandler)
    }
}
