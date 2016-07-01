package sx.logging.slf4j

import org.slf4j.Logger

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