package org.deku.leoz.bundle

import ch.qos.logback.classic.Level
import org.deku.leoz.config.BundleTestConfiguration
import org.deku.leoz.config.RsyncTestConfiguration
import org.deku.leoz.config.StorageTestConfiguration
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Created by masc on 24.08.15.
 */
@Ignore
class BundleRepositoryTest {

    val BUNDLE_NAME = "leoz-boot"

    init {
        val logger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
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
        BundleTestConfiguration.remoteRepository.upload(BundleType.LEOZ_BOOT.value, BundleTestConfiguration.releasePath)
    }

    @Test
    fun testDownload() {
        val path = File(StorageTestConfiguration.bundlesTestDirectory, BundleType.LEOZ_BOOT.value)

        BundleTestConfiguration.remoteRepository.download(
                BundleType.LEOZ_BOOT.value,
                BundleTestConfiguration.remoteRepository.listVersions(BundleType.LEOZ_BOOT.value).sortedDescending().first(),
                BundleTestConfiguration.releasePath)
    }
}