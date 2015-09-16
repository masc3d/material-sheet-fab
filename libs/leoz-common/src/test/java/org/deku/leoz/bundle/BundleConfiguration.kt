package org.deku.leoz.bundle

import sx.rsync.Rsync
import sx.rsync.RsyncClient
import java.nio.file.Paths

/**
 * Created by masc on 29.08.15.
 */
object BundleConfiguration {
    val rsyncUri = Rsync.URI("rsync://leoz@syntronix.de/leoz-test")
    val rsyncPw = "leoz"

    val path = Paths.get("").toAbsolutePath()
            .parent
            .parent
            .parent
            .resolve("leoz-release")
            .resolve("leoz-boot")

    val repository = BundleRepository(Bundles.LEOZ_BOOT, rsyncUri, rsyncPw)
}