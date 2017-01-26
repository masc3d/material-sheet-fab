package org.deku.leoz.bundle

import java.io.File

/**
 * Restart bundle process by invoking leoz-boot
 * @param bundleName Bundle name
 **/
fun BundleInstaller.boot(bundleName: String) {
    val leozBoot = Bundle.load(File(this.bundleContainerPath, BundleType.LEOZ_BOOT.value))
    leozBoot.execute(wait = false, args = *arrayOf("--no-ui", "--bundle", bundleName))
}