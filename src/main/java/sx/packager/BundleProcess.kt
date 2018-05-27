package sx.packager

import org.slf4j.LoggerFactory
import java.io.File


// Extension methods defining a process interface
// for installation and start/stop control

private val log = LoggerFactory.getLogger(Bundle::class.java)

fun Bundle.start() {
    log.info("Starting bundle process [${this.name}]")
    this.execute(args = *arrayOf("start"))
}

/**
 * Stop bundle process/service
 */
fun Bundle.stop() {
    log.info("Stopping bundle process [${this.name}]")
    this.execute(args = *arrayOf("stop"))
}

/**
 * Install bundle
 * @param productive Indicates the installation should prepare the environment to operate in a productive environment
 */
fun Bundle.install() {
    log.info("Installing bundle [${this.name}]")
    this.execute(args = *arrayOf("install"))
}

/**
 * Prepares bundle for operation in productive environment
 */
fun Bundle.prepareProduction() {
    log.info("Preparing productive environment for [${this.name}]")
    this.execute(args = *arrayOf("prepare-production"))
}

/**
 * Uninstall bundle
 */
fun Bundle.uninstall() {
    log.info("Uninstalling bundle [${this.name}]")
    this.execute(args = *arrayOf("uninstall"))
}
