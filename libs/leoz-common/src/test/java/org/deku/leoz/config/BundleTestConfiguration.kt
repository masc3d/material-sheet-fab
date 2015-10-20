package org.deku.leoz.config

import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.bundle.Bundles
import sx.rsync.Rsync
import sx.rsync.RsyncClient
import java.nio.file.Paths

/**
 * Created by masc on 29.08.15.
 */
object BundleTestConfiguration {
    val rsyncUri = Rsync.URI("rsync://leoz@syntronix.de/leoz-test")
    val rsyncPw = "leoz"

    val path = Paths.get("").toAbsolutePath()
            .parent
            .parent
            .parent
            .resolve("leoz-release")
            .resolve("test")

    val repository = BundleRepository(rsyncUri, rsyncPw)
}