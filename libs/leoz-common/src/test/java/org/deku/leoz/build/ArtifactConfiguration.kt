package org.deku.leoz.build

import sx.rsync.Rsync
import sx.rsync.RsyncClient
import java.nio.file.Paths

/**
 * Created by masc on 29.08.15.
 */
public object ArtifactConfiguration {
    val rsyncUri = Rsync.URI("rsync://leoz@syntronix.de/leoz")
    val rsyncPw = "leoz"

    val path = Paths.get("").toAbsolutePath()
            .getParent()
            .getParent()
            .getParent()
            .resolve("leoz-release")
            .resolve("leoz-boot")

    val repository = ArtifactRepository(Artifacts.LEOZ_BOOT, rsyncUri, rsyncPw)
}