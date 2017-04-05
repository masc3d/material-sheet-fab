package org.deku.leoz.bundle

import org.apache.commons.lang3.SystemUtils
import org.deku.leoz.config.BundleConfiguration
import org.deku.leoz.config.RsyncTestConfiguration
import org.junit.Test
import sx.packager.Bundle
import java.io.File

/**
 * Created by masc on 05.04.17.
 */
class BundleRepositoryTest {
    companion object {
        init {
            RsyncTestConfiguration.initialize()
        }
    }
    @Test
    fun testDownload() {
        val BUNDLE_NAME = "leoz-central"
        val path = File("build/download", BUNDLE_NAME)

        path.mkdirs()

        val repository = BundleConfiguration.stagingRepository
        repository.download(
                BUNDLE_NAME,
                repository.listVersions("leoz-central").first { it == Bundle.Version.parse("0.57-SNAPSHOT") },
                path)
    }
}