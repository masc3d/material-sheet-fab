package org.deku.leoz.bundle

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import org.apache.commons.lang3.SystemUtils
import org.deku.leoz.config.BundleConfiguration
import org.deku.leoz.config.RsyncTestConfiguration
import org.junit.Test
import sx.packager.Bundle
import sx.platform.CpuArch
import sx.platform.OperatingSystem
import sx.platform.PlatformId
import sx.rsync.Rsync
import java.io.File

/**
 * Created by masc on 05.04.17.
 */
class BundleRepositoryTest {
    companion object {
        init {
            Kodein.global.addImport(RsyncTestConfiguration.module)
        }
    }

    @Test
    fun testDownload() {
        val BUNDLE_NAME = "leoz-central"
        val path = File("build/download", BUNDLE_NAME)

        path.mkdirs()

        val repository = BundleConfiguration.stagingRepository

        try {
            repository.download(
                    bundleName = BUNDLE_NAME,
                    version = repository.listVersions("leoz-central").first(),
                    platformId = PlatformId(OperatingSystem.WINDOWS, CpuArch.X64),
                    destPath = path)
        } finally {
            println("Cleaning up")
            path.deleteRecursively()
        }
    }
}