package org.deku.leoz.bundle

import org.apache.commons.logging.LogFactory
import java.io.File


// Extension methods defining a process interface
// for installation and start/stop control

val log = LogFactory.getLog(Bundle::class.javaClass)

fun Bundle.start() {
    log.info("Starting bundle process [${this.name}]")
    this.execute(elevate = false, args = "start")
}

/**
 * Stop bundle process/service
 */
fun Bundle.stop() {
    log.info("Stopping bundle process [${this.name}]")
    this.execute(elevate = false, args = "stop")
}

/**
 * Install bundle
 */
fun Bundle.install() {
    log.info("Installing bundle [${this.name}]")
    this.execute(elevate = false, args = "install")
}

/**
 * Uninstall bundle
 */
fun Bundle.uninstall() {
    log.info("Uninstalling bundle [${this.name}]")
    this.execute(elevate = false, args = "uninstall")
}

/**
 * Restart bundle process by invoking leoz-boot
 * @param bundleName Bundle name
 **/
fun BundleInstaller.boot(bundleName: String) {
    val leozBoot = Bundle.load(
            BundleInstaller.getNativeBundlePath(File(this.bundleContainerPath, Bundles.LEOZ_BOOT)))

    leozBoot.execute(wait = false, args = *arrayOf("--no-ui", "--bundle", bundleName))
}