package org.deku.leoz.bundle

import org.apache.commons.logging.LogFactory

val log = LogFactory.getLog(Bundle::class.javaClass)

fun Bundle.start() {
    log.info("Starting bundle process [${this.name}]")
    this.execute("start")
}

/**
 * Stop bundle process/service
 */
fun Bundle.stop() {
    log.info("Stopping bundle process [${this.name}]")
    this.execute("stop")
}

/**
 * Install bundle
 */
fun Bundle.install() {
    log.info("Installing bundle [${this.name}]")
    this.execute("install")
}

/**
 * Uninstall bundle
 */
fun Bundle.uninstall() {
    log.info("Uninstalling bundle [${this.name}]")
    this.execute("uninstall")
}