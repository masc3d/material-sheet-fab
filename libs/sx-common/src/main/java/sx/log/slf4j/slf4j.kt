package sx.log.slf4j

import org.slf4j.Logger
import org.slf4j.bridge.SLF4JBridgeHandler
import java.util.logging.Level
import java.util.logging.LogManager

/**
 * Slf4j extensinos
 * Created by masc on 01/07/16.
 */
fun Logger.info(it: Any?) {
    this.info(it.toString())
}

fun Logger.debug(it: Any?) {
    this.debug(it.toString())
}

fun Logger.warn(it: Any?) {
    this.warn(it.toString())
}

fun Logger.error(it: Any?) {
    this.error(it.toString())
}

fun Logger.trace(it: Any?) {
    this.trace(it.toString())
}

/**
 * Helper for installing the JUL (java.util.logging) bridge,
 */
fun Logger.installJulBridge() {
    LogManager.getLogManager().reset()
    SLF4JBridgeHandler.removeHandlersForRootLogger()
    SLF4JBridgeHandler.install()
    LogManager.getLogManager().getLogger("").setLevel(Level.FINEST);
}