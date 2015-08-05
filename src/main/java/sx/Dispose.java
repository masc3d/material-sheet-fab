package sx;

import com.google.common.base.Stopwatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by masc on 07.07.15.
 */
public class Dispose {
    static Log mLog = LogFactory.getLog(Dispose.class);

    /**
     * Customized disposal
     * @param r Dispose operation
     */
    public static void safely(Runnable r, Action<Exception> exceptionHandler) {
        try {
            r.run();
        } catch (Exception e) {
            if (exceptionHandler != null)
                exceptionHandler.perform(e);
        }
    }

    /**
     * Safely disposes an instance implementing @link Disposable, logging, timing and errors
     * @param d Disposable instance
     * @param exceptionHandler Optional exception handler
     */
    public static void safely(Disposable d, Action<Exception> exceptionHandler) {
        mLog.info(String.format("Disposing [%s]", d.getClass().getName()));
        Stopwatch sw = Stopwatch.createStarted();
        safely((Runnable) () -> d.dispose(), (e) -> {
            mLog.error(e.getMessage(), e);
            exceptionHandler.perform(e);
        }
        );
        mLog.info(String.format("Disposed [%s] in [%s]", d.getClass().getName(), sw.toString()));
    }

    /**
     * Safely disposes an instance implementing @link Disposable, logging timing and errors
     * @param d
     */
    public static void safely(Disposable d) {
        safely(d, null);
    }
}
