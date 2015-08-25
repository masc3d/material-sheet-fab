package org.deku.leoz.build

import org.apache.commons.lang3.SystemUtils
import org.deku.leoz.RsyncConfiguration
import org.junit.Test
import sx.rsync.RsyncClient
import java.net.URI
import java.nio.file.Paths

/**
 * Created by masc on 24.08.15.
 */
public class ArtifactRepositoryTest {
    val rsyncUri = RsyncClient.URI("rsync://leoz@syntronix.de/leoz")
    val rsyncPw = "leoz"

    init {
        RsyncConfiguration.initialize()
    }

    @Test
    public fun testList() {
        var ar = ArtifactRepository(Artifact.Type.LEOZ_BOOT, rsyncUri, rsyncPw)
        var versions = ar.list()
        versions.forEach { println(it) }
    }
}