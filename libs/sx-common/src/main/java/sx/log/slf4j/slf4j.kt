package sx.log.slf4j

import org.slf4j.Logger
import org.slf4j.Marker
import org.slf4j.bridge.SLF4JBridgeHandler
import org.slf4j.event.Level
import java.util.logging.LogManager

//region Lazy message evaluation
inline fun Logger.message(level: Level, it: () -> Any?) {
    when (level) {
        Level.ERROR -> this.error(it)
        Level.WARN -> this.warn(it)
        Level.INFO -> this.info(it)
        Level.DEBUG -> this.debug(it)
        Level.TRACE -> this.trace(it)
    }
}

inline fun Logger.error(it: () -> Any?) {
    if (isErrorEnabled) error(it().toString())
}

inline fun Logger.warn(it: () -> Any?) {
    if (isWarnEnabled) warn(it().toString())
}

inline fun Logger.info(it: () -> Any?) {
    if (isInfoEnabled) info(it().toString())
}

inline fun Logger.debug(it: () -> Any?) {
    if (isDebugEnabled) debug(it().toString())
}

inline fun Logger.trace(it: () -> Any?) {
    if (isTraceEnabled) trace(it().toString())
}

inline fun Logger.error(marker: Marker?, it: () -> Any?) {
    if (isErrorEnabled(marker)) error(marker, it().toString())
}

inline fun Logger.warn(marker: Marker?, it: () -> Any?) {
    if (isWarnEnabled(marker)) warn(marker, it().toString())
}

inline fun Logger.info(marker: Marker?, it: () -> Any?) {
    if (isInfoEnabled(marker)) info(marker, it().toString())
}

inline fun Logger.debug(marker: Marker?, it: () -> Any?) {
    if (isDebugEnabled(marker)) debug(marker, it().toString())
}

inline fun Logger.trace(marker: Marker?, it: () -> Any?) {
    if (isTraceEnabled(marker)) trace(marker, it().toString())
}

inline fun Logger.error(throwable: Throwable?, it: () -> Any?) {
    if (isErrorEnabled) error(it().toString(), throwable)
}

inline fun Logger.warn(throwable: Throwable?, it: () -> Any?) {
    if (isWarnEnabled) warn(it().toString(), throwable)
}

inline fun Logger.info(throwable: Throwable?, it: () -> Any?) {
    if (isInfoEnabled) info(it().toString(), throwable)
}

inline fun Logger.debug(throwable: Throwable?, it: () -> Any?) {
    if (isDebugEnabled) debug(it().toString(), throwable)
}

inline fun Logger.trace(throwable: Throwable?, it: () -> Any?) {
    if (isTraceEnabled) trace(it().toString(), throwable)
}

inline fun Logger.error(marker: Marker?, throwable: Throwable?, it: () -> Any?) {
    if (isErrorEnabled(marker)) error(marker, it().toString(), throwable)
}

inline fun Logger.warn(marker: Marker?, throwable: Throwable?, it: () -> Any?) {
    if (isWarnEnabled(marker)) warn(marker, it().toString(), throwable)
}

inline fun Logger.info(marker: Marker?, throwable: Throwable?, it: () -> Any?) {
    if (isInfoEnabled(marker)) info(marker, it().toString(), throwable)
}

inline fun Logger.debug(marker: Marker?, throwable: Throwable?, it: () -> Any?) {
    if (isDebugEnabled(marker)) debug(marker, it().toString(), throwable)
}

inline fun Logger.trace(marker: Marker?, throwable: Throwable?, it: () -> Any?) {
    if (isTraceEnabled(marker)) trace(marker, it().toString(), throwable)
}
//endregion

fun Logger.message(level: Level, it: String) {
    this.message(level, { it })
}

/**
 * Helper for installing the JUL (java.util.logging) bridge,
 */
fun Logger.installJulBridge() {
    LogManager.getLogManager().reset()
    SLF4JBridgeHandler.removeHandlersForRootLogger()
    SLF4JBridgeHandler.install()
    LogManager.getLogManager().getLogger("").setLevel(java.util.logging.Level.FINEST);
}
