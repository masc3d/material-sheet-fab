package sx.packager

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.packager.config.BundleTestConfiguration
import sx.packager.config.RsyncTestConfiguration
import java.io.File

/**
 * BundleRepository tests
 * Created by masc on 24.08.15.
 */
class BundleRepositoryTest {

    val BUNDLE_NAME = "leoz-boot"

    init {
        val logger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger
        logger.level = Level.TRACE

        RsyncTestConfiguration.initialize()
    }

    @Test
    fun testListBundles() {
        val bundles = BundleTestConfiguration.remoteRepository.listBundles()
        bundles.forEach { println(it) }
    }
    @Test
    fun testListVersions() {
        val versions = BundleTestConfiguration.remoteRepository.listVersions(BUNDLE_NAME)
        versions.forEach { println(it) }
    }

    @Test
    fun testListPlatforms() {
        val version = BundleTestConfiguration.remoteRepository.listVersions(BUNDLE_NAME).first()
        val platforms = BundleTestConfiguration.remoteRepository.listPlatforms(BUNDLE_NAME, version)
        platforms.forEach { println(it) }
    }

    @Test
    fun testUpload() {
        BundleTestConfiguration.remoteRepository.upload(BUNDLE_NAME, BundleTestConfiguration.releasePath)
    }

    @Test
    fun testDownload() {
        val path = File("build/download", BUNDLE_NAME)

        BundleTestConfiguration.remoteRepository.download(
                BUNDLE_NAME,
                BundleTestConfiguration.remoteRepository.listVersions(BUNDLE_NAME).sortedDescending().first(),
                BundleTestConfiguration.releasePath)
    }
}