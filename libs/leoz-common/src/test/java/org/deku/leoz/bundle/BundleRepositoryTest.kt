package org.deku.leoz.bundle

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
class BundleRepositoryTest {

    init {
        var logger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
        logger.level = Level.TRACE

        RsyncConfiguration.initialize()
    }

    @Test
    fun testList() {
        var versions = BundleConfiguration.repository.listVersions()
        versions.forEach { println(it) }
    }

    @Test
    fun testListPlatforms() {
        var platforms = BundleConfiguration.repository.listPlatforms(Bundle.Version.parse("0.1"))
        platforms.forEach { println(it) }
    }

    @Test
    fun testUpload() {
        BundleConfiguration.repository.upload(BundleConfiguration.path.toFile())
    }

    @Test
    fun testDownload() {
        var path = BundleConfiguration.path
                .resolve(PlatformId.current().toString()).toFile()

        BundleConfiguration.repository.download(Bundle.Version.parse("0.1"), PlatformId.current(), path)
    }
}