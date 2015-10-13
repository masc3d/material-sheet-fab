package org.deku.leoz.bundle

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.apache.commons.lang3.SystemUtils
import org.deku.leoz.config.TestBundleConfiguration
import org.deku.leoz.config.TestRsyncConfiguration
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

        TestRsyncConfiguration.initialize()
    }

    @Test
    fun testList() {
        var versions = TestBundleConfiguration.repository.listVersions()
        versions.forEach { println(it) }
    }

    @Test
    fun testListPlatforms() {
        var platforms = TestBundleConfiguration.repository.listPlatforms(Bundle.Version.parse("0.1"))
        platforms.forEach { println(it) }
    }

    @Test
    fun testUpload() {
        TestBundleConfiguration.repository.upload(TestBundleConfiguration.path.toFile())
    }

    @Test
    fun testDownload() {
        var path = TestBundleConfiguration.path
                .resolve(PlatformId.current().toString()).toFile()

        TestBundleConfiguration.repository.download(Bundle.Version.parse("0.1"), PlatformId.current(), path)
    }
}