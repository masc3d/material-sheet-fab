package org.deku.leoz.bundle

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.apache.commons.lang3.SystemUtils
import org.deku.leoz.config.BundleTestConfiguration
import org.deku.leoz.config.RsyncTestConfiguration
import org.deku.leoz.config.StorageTestConfiguration
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.io.PermissionUtil
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

        RsyncTestConfiguration.initialize()
    }

    @Test
    fun testList() {
        var versions = BundleTestConfiguration.repository.listVersions("test")
        versions.forEach { println(it) }
    }

    @Test
    fun testListPlatforms() {
        var platforms = BundleTestConfiguration.repository.listPlatforms("test", Bundle.Version.parse("0.1"))
        platforms.forEach { println(it) }
    }

    @Test
    fun testUpload() {
        BundleTestConfiguration.repository.upload(Bundles.LEOZ_BOOT, BundleTestConfiguration.releasePath)
    }

    @Test
    fun testDownload() {
        var path = File(StorageTestConfiguration.bundlesDirectory, Bundles.LEOZ_BOOT)

        BundleTestConfiguration.repository.download(
                Bundles.LEOZ_BOOT,
                BundleTestConfiguration.repository.listVersions(Bundles.LEOZ_BOOT).sortedDescending().first(),
                BundleTestConfiguration.releasePath)
    }
}