package org.deku.leoz

import org.deku.leoz.bundle.Bundle
import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.bundle.Bundles
import org.deku.leoz.config.StorageConfiguration
import java.io.File
import java.nio.channels.FileLock

/**
 * Provides methods to (re)start a bundle process using leoz-boot
 * Created by masc on 23.10.15.
 * @param bundleContainerPath Bundle container path
 */
class Boot(
        val bundleContainerPath: File
) {
    /** Restart bundle process */
    fun boot(bundleName: String) {
        val leozBoot = Bundle.load(
                BundleInstaller.getNativeBundlePath(File(this.bundleContainerPath, Bundles.LEOZ_BOOT)))

        leozBoot.execute(wait = false, args = bundleName)
    }
}