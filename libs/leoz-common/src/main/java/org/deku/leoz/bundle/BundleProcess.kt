package org.deku.leoz.bundle

import org.apache.commons.logging.LogFactory
import java.io.File
import java.util.function.Supplier


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
 * Install bundle from remote repository
 * @param version Version to install
 * @param onProgress Progress callback
 */
fun BundleInstaller.install(version: Supplier<Bundle.Version>? = null,
                            onProgress: ((file: String, percentage: Double) -> Unit) = fun(f, p) {
                            }) {
    if (this.hasBundle()) {
        this.bundle.stop()
        this.bundle.uninstall()
    }

    val versionToInstall: Bundle.Version

    when (version) {
        null -> {
            log.info("Checking for available versions of [${this.bundleName}]")
            versionToInstall = this.repository
                    .listVersions(this.bundleName)
                    .sortedDescending()
                    .first()
        }
        else -> versionToInstall = version.get()
    }

    log.info("Installing [${bundleName}-${versionToInstall}]")
    this.download(versionToInstall, false, { f, p ->
        onProgress(f, p)
    })

    this.bundle.install()
    this.bundle.start()

    log.info("Installed sucessfully.")
}

/**
 * Restart bundle process by invoking leoz-boot
 **/
fun BundleInstaller.boot() {
    val leozBoot = Bundle.load(
            BundleInstaller.getNativeBundlePath(File(this.bundleContainerPath, Bundles.LEOZ_BOOT)))

    leozBoot.execute(wait = false, args = *arrayOf("--no-ui", this.bundleName))
}