package org.deku.leoz.config

import org.deku.leoz.bundle.BundleRepository
import sx.rsync.Rsync
import java.nio.file.Paths

/**
 * Created by masc on 29.08.15.
 */
object BundleTestConfiguration {
    val rsyncUri = Rsync.URI("rsync://leoz@syntronix.de/leoz-test")
    val rsyncPw = "leoz"

    val releasePath = Paths.get("").toAbsolutePath()
            .parent
            .parent
            .resolve("release")
            .resolve("test")
            .toFile()

    val remoteRepository = BundleRepository(rsyncUri, rsyncPw)
}