package org.deku.leoz.build

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.apache.commons.lang3.SystemUtils
import org.deku.leoz.RsyncConfiguration
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.platform.PlatformId
import sx.rsync.RsyncClient
import java.io.File
import java.net.URI
import java.nio.file.Paths

/**
 * Created by masc on 24.08.15.
 */
@Ignore
public class ArtifactRepositoryTest {
    val rsyncUri = RsyncClient.URI("rsync://leoz@syntronix.de/leoz")
    val rsyncPw = "leoz"

    val artifactPath = Paths.get("").toAbsolutePath()
            .getParent()
            .getParent()
            .getParent()
            .resolve("leoz-release")
            .resolve("leoz-boot")

    init {
        var logger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
        logger.setLevel(Level.TRACE)

        RsyncConfiguration.initialize()
    }

    @Test
    public fun testList() {
        var ar = ArtifactRepository(Artifact.Type.LEOZ_BOOT, rsyncUri, rsyncPw)
        var versions = ar.list()
        versions.forEach { println(it) }
    }

    @Test
    public fun testUpload() {
        var ar = ArtifactRepository(Artifact.Type.LEOZ_BOOT, rsyncUri, rsyncPw)

        ar.upload(artifactPath.toFile(), Artifact.Version.parse("0.1"))
    }

    @Test
    public fun testDownload() {
        var ar = ArtifactRepository(Artifact.Type.LEOZ_BOOT, rsyncUri, rsyncPw)

        var path = artifactPath
                .resolve(PlatformId.current().toString()).toFile()

        ar.download(Artifact.Version.parse("0.1"), PlatformId.current(), path)
    }

    @Test
    public fun testCreate() {
        var path = artifactPath.resolve(PlatformId.current().toString())

        Artifact.create(path.toFile(), Artifact.Type.LEOZ_BOOT.toString(), Artifact.Version.parse("0.1"))
    }

    @Test
    public fun testLoad() {
        var path = artifactPath.resolve(PlatformId.current().toString())

        Artifact.load(path.toFile())
    }

    @Test
    public fun testVerify() {
        var path = artifactPath.resolve(PlatformId.current().toString())

        Artifact.load(path.toFile()).verify()
    }
}