package sx

import com.google.common.base.Stopwatch
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * Created by masc on 07.07.15.
 */
object Dispose {
    internal var log = LogFactory.getLog(Dispose::class.java)

    /**
     * Customized disposal
     * @param r Dispose operation
     */
    fun safely(r: Runnable, exceptionHandler: Action<Exception>?) {
        try {
            r.run()
        } catch (e: Exception) {
            exceptionHandler?.perform(e)
        }

    }

    /**
     * Safely disposes an instance implementing @link Disposable, logging, timing and errors
     * @param d Disposable instance
     * @param exceptionHandler Optional exception handler
     */
    @JvmOverloads fun safely(d: Disposable, exceptionHandler: Action<Exception>? = null) {
        log.info(String.format("Disposing [%s]", d.javaClass.name))
        val sw = Stopwatch.createStarted()
        safely(Runnable { d.close() }, Action<java.lang.Exception> { e ->
            log.error(e.message, e)
            exceptionHandler!!.perform(e)
        })
        log.info(String.format("Disposed [%s] in [%s]", d.javaClass.name, sw.toString()))
    }

    /**
     * Safely disposes an instance implementing @link Disposable, logging, timing and errors
     * @param d Disposable instance
     * @param exceptionHandler Optional exception handler
     */
    @JvmOverloads fun safely(d: sx.legacy.Disposable, exceptionHandler: Action<Exception>? = null) {
        this.safely(object : Disposable {
            override fun close() {
                d.close()
            }
        }, exceptionHandler)
    }
}
